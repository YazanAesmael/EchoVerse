package com.jetpackages.echoverse.core.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import com.jetpackages.echoverse.core.domain.model.PersonalityProfile
import kotlinx.datetime.Clock
import model.Message
import repository.ChatRepository
import usecase.RetrieveMemoriesUseCase

class KoogChatRepository(
    private val geminiExecutor: PromptExecutor,
    private val retrieveMemoriesUseCase: RetrieveMemoriesUseCase
) : ChatRepository {
    override suspend fun getReply(
        profile: PersonalityProfile,
        history: List<Message>,
        userMessage: String
    ): Message {
        val allMemories = retrieveMemoriesUseCase(profile.echoId)
        val relevantMemories = findRelevantMemories(
            userQuery = userMessage,
            memoryBank = allMemories
        )

        // We dynamically build the system prompt from the structured profile for every message.
        val dynamicSystemPrompt = buildString {
            appendLine("You are an AI emulating a person named '${profile.coreIdentity.name}'. You MUST adopt their personality and communication style. Follow these instructions precisely.")
            appendLine("\n### Core Identity & Context")
            appendLine("- Your relationship to the user is: ${profile.coreIdentity.relationshipToUser}")
            appendLine("- Your general life situation is: ${profile.coreIdentity.dailyLifeSummary}")

            appendLine("\n### Behavioral Directives (Strict Rules)")
            appendLine("- Message Length Rule: ${profile.communicationRules.averageMessageLength}")
            appendLine("- Tone Rule: ${profile.communicationRules.tone}")
            appendLine("- Capitalization Rule: ${profile.communicationRules.capitalizationStyle}")
            appendLine("- Punctuation Rule: ${profile.communicationRules.punctuationStyle}")
            appendLine("- Emoji Frequency Rule: ${profile.communicationRules.emojiFrequency}")

            appendLine("\n### Lexicon (Use these specific words and emojis)")
            appendLine("- Signature Phrases to use: ${profile.lexicon.signaturePhrases.joinToString(", ")}")
            appendLine("- Terms of Endearment to use: ${profile.lexicon.termsOfEndearment.joinToString(", ")}")
            appendLine("- Preferred Emojis: ${profile.lexicon.topEmojis.joinToString(" ")}")

            if (relevantMemories.isNotBlank()) {
                appendLine("\n### RELEVANT MEMORIES (FOR CONTEXT):")
                appendLine("Here are some relevant snippets from past conversations to inform your reply. Do not mention that these are memories; just use the information naturally.")
                append(relevantMemories)
            }
        }

        val conversationalPrompt = prompt("echo-chat-rag") {
            system(dynamicSystemPrompt)
            history.takeLast(20).forEach { message ->
                if (message.isFromUser) user(message.text) else assistant(message.text)
            }
            user(userMessage)
        }

        // 4. We call 'executeAndGetContent', which is the simplest text-in, text-out function.
        //    It avoids tool-calling and returns a plain String.
        val aiText = geminiExecutor.execute(
            prompt = conversationalPrompt,
            model = mainModel,
            tools = emptyList()
        ).getOrNull(0)?.content ?: "No Message"

        return Message(
            text = aiText,
            isFromUser = false,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }

    /**
     * A sub-agent that uses an LLM to perform a semantic search over the memory bank.
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
            model = mainModel,
            tools = emptyList()
        ).getOrNull(0)?.content ?: "No Message"
    }
}