package com.jetpackages.echoverse.core.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
import com.jetpackages.echoverse.core.ai.schema.ParticipantListSchema
import com.jetpackages.echoverse.core.ai.schema.StructuredProfileSchema
import com.jetpackages.echoverse.core.domain.model.CommunicationRules
import com.jetpackages.echoverse.core.domain.model.CoreIdentity
import com.jetpackages.echoverse.core.domain.model.Lexicon
import com.jetpackages.echoverse.core.domain.model.PersonalityProfile
import kotlinx.datetime.Clock
import model.ChatParticipant
import repository.ChatAnalyzer
import kotlin.random.Random

val mainModel = GoogleModels.Gemini1_5ProLatest
val fixingModel = GoogleModels.Gemini1_5ProLatest

/**
 * The concrete implementation of ChatAnalyzer using the Koog framework.
 * This class orchestrates specialized AI agents to perform analysis tasks.
 *
 * @param geminiExecutor A prompt executor configured for Google's Gemini models.
 */
class KoogChatAnalyzer(
    private val geminiExecutor: PromptExecutor,
) : ChatAnalyzer {

    // Blueprint for the participant list response.
    private val participantListStructure = JsonStructuredData.createJsonStructure<ParticipantListSchema>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    private val structuredProfileBlueprint = JsonStructuredData.createJsonStructure<StructuredProfileSchema>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    override suspend fun extractParticipants(chatContent: String): List<ChatParticipant> {
        val systemPrompt = """
            You are a highly specialized chat log parser. Your only task is to identify the unique
            names of the message senders in a given chat history text.

            The chat log follows a specific format for each message:
            `[DD/MM/YY, HH:MM:SS] Sender Name: Message content...`

            Your job is to parse the 'Sender Name' from each line that has one and return a unique list
            of all senders.

            **Example Input:**
            `[01/01/25, 10:00:00] Alice: Hey! How are you?`
            `[01/01/25, 10:00:05] Bob: I'm good, thanks! You?`
            `[01/01/25, 10:00:10] Alice: Doing great!`
            `[01/01/25, 10:00:15] Some system message`

            **Correct JSON Output for Example:**
            `{ "participants": ["Alice", "Bob"] }`

            Do not extract names mentioned within the message content. Only extract the 'Sender Name'
            that appears after the timestamp and before the colon. Ignore system messages that
            do not have a sender name. The chat provided is a one-on-one conversation.
        """.trimIndent()

        val result = geminiExecutor.executeStructured(
            prompt = prompt("participant-extractor") {
                system(systemPrompt)
                user(chatContent)
            },
            structure = participantListStructure,
            mainModel = mainModel,
            fixingModel = fixingModel
        )

        val resultOrNull = result.getOrNull()


        print("--\n\nGenerated text: $resultOrNull\n\n--")

        return resultOrNull
            ?.structure
            ?.participants
            ?.map { name -> ChatParticipant(name) }
            ?: emptyList()
    }

    override suspend fun synthesizeProfile(
        chatContent: String,
        participantName: String,
        sourceChatFileName: String
    ): PersonalityProfile {
        // This new prompt is streamlined to focus the AI on filling the JSON object.
        val systemPrompt = """
            You are a data extraction service. Your sole purpose is to analyze the provided chat log
            and populate a JSON object with a structured personality analysis of the participant named '$participantName'.
            Focus ONLY on messages from '$participantName'.
            Analyze their vocabulary, tone, emoji usage, and relationship to the other user to fill
            the fields as accurately as possible. Adhere strictly to the requested JSON schema.
        """.trimIndent()

        // Execute the agent call, requesting the new structured schema.
        val result = geminiExecutor.executeStructured(
            prompt = prompt("structured-profile-synthesizer") {
                system(systemPrompt)
                user(chatContent)
            },
            structure = structuredProfileBlueprint,
            mainModel = GoogleModels.Gemini1_5ProLatest,
            fixingModel = GoogleModels.Gemini1_5ProLatest
        )

        val aiResult = result.getOrNull()?.structure
            ?: // Handle the case where the AI fails to generate a valid structure
            return createFallbackProfile(participantName, sourceChatFileName)

        // Map the AI's structured response into our clean domain model.
        return PersonalityProfile(
            id = generateUniqueId(),
            echoId = generateUniqueId(),
            sourceChatFileName = sourceChatFileName,
            coreIdentity = CoreIdentity(
                name = aiResult.coreIdentity.name,
                relationshipToUser = aiResult.coreIdentity.relationshipToUser,
                dailyLifeSummary = aiResult.coreIdentity.dailyLifeSummary
            ),
            communicationRules = CommunicationRules(
                averageMessageLength = aiResult.communicationRules.averageMessageLength,
                tone = aiResult.communicationRules.tone,
                capitalizationStyle = aiResult.communicationRules.capitalizationStyle,
                punctuationStyle = aiResult.communicationRules.punctuationStyle,
                emojiFrequency = aiResult.communicationRules.emojiFrequency
            ),
            lexicon = Lexicon(
                signaturePhrases = aiResult.lexicon.signaturePhrases,
                termsOfEndearment = aiResult.lexicon.termsOfEndearment,
                topEmojis = aiResult.lexicon.topEmojis
            )
        )
    }

    private fun createFallbackProfile(name: String, sourceFile: String): PersonalityProfile {
        // Provides a safe, default profile if the AI fails.
        return PersonalityProfile(
            id = generateUniqueId(), echoId = generateUniqueId(), sourceChatFileName = sourceFile,
            coreIdentity = CoreIdentity(name, "Unknown", "Could not analyze daily life."),
            communicationRules = CommunicationRules("Average", "Neutral", "Standard", "Standard", "Average"),
            lexicon = Lexicon(emptyList(), emptyList(), emptyList())
        )
    }

    // A simple helper to generate unique IDs. In a real app, you might use a UUID library.
    private fun generateUniqueId(): String {
        return Clock.System.now().toEpochMilliseconds().toString() + Random.nextLong().toString()
    }
}