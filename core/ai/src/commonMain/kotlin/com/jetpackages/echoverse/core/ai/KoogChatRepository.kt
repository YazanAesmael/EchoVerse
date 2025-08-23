package com.jetpackages.echoverse.core.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.model.PromptExecutor
import com.jetpackages.echoverse.core.ai.cognitive.EmotionalAnalyzerTool
import com.jetpackages.echoverse.core.ai.cognitive.StyleAdvisorTool
import com.jetpackages.echoverse.core.domain.util.randomFloat
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import model.Message
import model.PersonalityProfile
import repository.ChatRepository
import usecase.RetrieveMemoriesUseCase

/**
 * A sophisticated chat repository that implements the "Cognitive Funnel" pattern.
 * It orchestrates multiple AI tools to generate a highly contextual and nuanced reply.
 */
class KoogChatRepository(
    private val geminiExecutor: PromptExecutor,
    private val retrieveMemoriesUseCase: RetrieveMemoriesUseCase,
    private val emotionalAnalyzer: EmotionalAnalyzerTool,
    private val styleAdvisor: StyleAdvisorTool
) : ChatRepository {

    override suspend fun getReply(
        profile: PersonalityProfile,
        history: List<Message>,
        userMessage: String
    ): Message {
        Napier.d(tag = "CognitiveFunnel") { "--- NEW MESSAGE ---" }
        Napier.d(tag = "CognitiveFunnel") { "User Input: '$userMessage'" }

        // 1a. Analyze Emotion
        val lastFewMessages = (history.takeLast(2).map {
            "${if(it.isFromUser) "User" else profile.coreIdentity.name}: ${it.text}"
        } + "User: $userMessage").joinToString("\n")
        val emotionAnalysis = emotionalAnalyzer.analyze(lastFewMessages)
        Napier.d(tag = "CognitiveFunnel") { "Step 1a | Emotion Analysis: $emotionAnalysis" }

        // 1b. Retrieve Long-Term Memory (RAG)
        val allMemories = retrieveMemoriesUseCase(profile.echoId)
        val relevantMemories = findRelevantMemories(userMessage, allMemories)
        Napier.d(tag = "CognitiveFunnel") { "Step 1b | Relevant Memories Retrieved:\n$relevantMemories" }

        // 1c. Get Stylistic Advice for this specific turn.
        val styleAdvice = styleAdvisor.advise(profile)
        Napier.d(tag = "CognitiveFunnel") { "Step 1c | Style Advice: $styleAdvice" }

        // --- STEP 2: DYNAMIC PROMPT ASSEMBLY ---
        val dynamicSystemPrompt = buildString {
            appendLine("You are an AI emulating '${profile.coreIdentity.name}'. You MUST adopt their personality and communication style. Your goal is a natural, in-character response.")
            appendLine("\n### BRIEFING FOR THIS REPLY:")
            appendLine("- The user's current mood seems: ${emotionAnalysis.mood} (Urgency: ${emotionAnalysis.urgency})")
            appendLine("- Your stylistic advice is: Tone should be '${styleAdvice.tone}'. Length should be '${styleAdvice.length}'.")

            if (relevantMemories.isNotBlank()) {
                appendLine("\n### RELEVANT MEMORIES (FOR CONTEXT):")
                appendLine("Subtly weave information from these past conversations into your reply if it feels natural. DO NOT explicitly say 'I remember when...'.")
                append(relevantMemories)
            }
        }

        val conversationalPrompt = prompt("cognitive-chat-prompt") {
            system(dynamicSystemPrompt)
            history.takeLast(10).forEach { message ->
                if (message.isFromUser) user(message.text) else assistant(message.text)
            }
            user(userMessage)
        }

        Napier.d(tag = "CognitiveFunnel") { "Step 2 | Final System Prompt:\n$dynamicSystemPrompt" }

        // --- STEP 3: GENERATE RESPONSE ---
        var aiText = geminiExecutor.execute(
            prompt = conversationalPrompt,
            model = GoogleModels.Gemini1_5ProLatest,
            tools = emptyList()
        ).getOrNull(0)?.content ?: "No Content"

        Napier.d(tag = "CognitiveFunnel") { "Step 3 | Raw AI Response: '$aiText'" }

        // --- STEP 4: PROBABILISTIC DECISION (CORRECTED LOGGING) ---
        val emojiRoll = randomFloat()
        // This regex is a standard way to detect most common emojis.
        val emojiRegex = Regex("\\p{So}")
        // Check if the AI's raw response contains ANY emoji.
        val alreadyHasEmoji = emojiRegex.containsMatchIn(aiText)

        if (!alreadyHasEmoji && emojiRoll < styleAdvice.emojiProbability && profile.lexicon.topEmojis.isNotEmpty()) {
            val emojiToAdd = profile.lexicon.topEmojis.random()
            aiText += " $emojiToAdd"
            Napier.d(tag = "CognitiveFunnel") { "Step 4 | Emoji check PASSED (rolled $emojiRoll < ${styleAdvice.emojiProbability} AND no emoji present). Added: $emojiToAdd" }
        } else {
            val reason = if (alreadyHasEmoji) "AI already added one" else "rolled $emojiRoll >= ${styleAdvice.emojiProbability}"
            Napier.d(tag = "CognitiveFunnel") { "Step 4 | Emoji check SKIPPED ($reason)." }
        }

        return Message(
            text = aiText,
            isFromUser = false,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }

    /**
     * A sub-agent that uses an LLM to perform a semantic search over the memory bank.
     * This is the "Retrieval" part of RAG.
     */
    private suspend fun findRelevantMemories(userQuery: String, memoryBank: List<String>): String {
        if (memoryBank.isEmpty()) return ""

        val memoryList = memoryBank.joinToString("\n") { "- $it" }

        val systemPrompt = """
            You are a relevance analysis AI. Your task is to select up to 3 of the most relevant
            conversation snippets from the provided MEMORY BANK that are semantically related to the
            CURRENT USER QUERY. Return only the selected snippets, each on a new line.
            If no snippets are relevant, return an empty string.
        """.trimIndent()

        val userPrompt = """
            MEMORY BANK:
            $memoryList

            CURRENT USER QUERY:
            "$userQuery"
        """.trimIndent()

        return geminiExecutor.execute(
            prompt = prompt("relevance-analysis") {
                system(systemPrompt)
                user(userPrompt)
            },
            model = GoogleModels.Gemini1_5FlashLatest,
            tools = emptyList()
        ).getOrNull(0)?.content ?: "No Content"
    }
}