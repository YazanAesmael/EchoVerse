package com.jetpackages.echoverse.core.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import com.jetpackages.echoverse.core.ai.cognitive.EmotionalAnalyzerTool
import com.jetpackages.echoverse.core.ai.cognitive.StyleAdvisorTool
import com.jetpackages.echoverse.core.domain.util.randomFloat
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import model.Message
import model.PersonalityProfile
import repository.ChatRepository
import repository.ConversationArchive
import usecase.RetrieveMemoriesUseCase

/**
 * A sophisticated chat repository that implements the "Cognitive Funnel" pattern.
 * It orchestrates multiple AI tools to generate a highly contextual and nuanced reply.
 */
class KoogChatRepository(
    private val geminiExecutor: PromptExecutor,
    private val conversationArchive: ConversationArchive
) : ChatRepository {

    override suspend fun getReply(
        profile: PersonalityProfile,
        history: List<Message>,
        userMessage: String
    ): Message {
        Napier.d(tag = "ExemplarEngine") { "--- NEW MESSAGE ---" }
        Napier.d(tag = "ExemplarEngine") { "User Input: '$userMessage'" }

        // --- STEP 1: RETRIEVE EXEMPLARS (The new RAG) ---
        val fullArchive = conversationArchive.retrieveAll(profile.echoId)
        val exemplars = findExemplars(
            userQuery = userMessage,
            conversationArchive = fullArchive,
            personNameToFind = profile.coreIdentity.name
        )
        Napier.d(tag = "ExemplarEngine") { "Step 1 | Retrieved Exemplars:\n$exemplars" }

        // --- STEP 2: DYNAMIC PROMPT ASSEMBLY ---
        val dynamicSystemPrompt = buildString {
            appendLine("You are an AI emulating '${profile.coreIdentity.name}'. Your PRIMARY GOAL is to reply to the user's message in a way that is consistent with the provided EXEMPLARS. The exemplars are real examples of how '${profile.coreIdentity.name}' talks. Match their tone, length, vocabulary, and emoji usage as closely as possible.")
            appendLine("\n### HIGH-LEVEL PERSONA (Static DNA):")
            appendLine("- Your relationship to the user is: ${profile.coreIdentity.relationshipToUser}")
            appendLine("- Your primary tone is: ${profile.communicationRules.tone}")

            if (exemplars.isNotBlank()) {
                appendLine("\n### EXEMPLARS FOR THIS REPLY (Learn from these):")
                append(exemplars)
            }
        }
        Napier.d(tag = "ExemplarEngine") { "Step 2 | Final System Prompt:\n$dynamicSystemPrompt" }

        val conversationalPrompt = prompt("exemplar-chat-prompt") {
            system(dynamicSystemPrompt)
            history.takeLast(10).forEach { message ->
                if (message.isFromUser) user(message.text) else assistant(message.text)
            }
            user(userMessage)
        }

        // --- STEP 3: GENERATE RESPONSE ---
        val aiText = geminiExecutor.execute(
            prompt = conversationalPrompt,
            model = mainModel,
            tools = emptyList()
        ).getOrNull(0)?.content ?: ""
        Napier.d(tag = "ExemplarEngine") { "Step 3 | Final AI Response: '$aiText'" }

        return Message(
            text = aiText.trim(),
            isFromUser = false,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }

    /**
     * The new "Exemplar Finder" sub-agent.
     */
    private suspend fun findExemplars(userQuery: String, conversationArchive: List<String>, personNameToFind: String): String {
        if (conversationArchive.isEmpty()) return ""

        val archiveSample = conversationArchive.take(50).joinToString("\n---\n")

        val systemPrompt = """
            You are a 'Dialogue Example Finder' AI. Your task is to search the provided CONVERSATION ARCHIVE. Find 2-3 of the best, most representative conversation snippets that show how '$personNameToFind' typically replies. The snippets should be semantically related to the CURRENT USER QUERY.

            A good snippet contains the user's message and '$personNameToFind's' immediate reply.

            Return ONLY the verbatim snippets, formatted exactly as they appear in the archive. If no relevant examples are found, return an empty string.
        """.trimIndent()

        val userPrompt = """
            CONVERSATION ARCHIVE:
            $archiveSample

            CURRENT USER QUERY:
            "$userQuery"
        """.trimIndent()

        return geminiExecutor.execute(
            prompt = prompt("exemplar-finder") {
                system(systemPrompt)
                user(userPrompt)
            },
            model = mainModel,
            tools = emptyList()
        ).getOrNull(0)?.content ?: ""
    }
}