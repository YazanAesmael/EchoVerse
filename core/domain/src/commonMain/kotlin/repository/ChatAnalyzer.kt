package repository

import com.jetpackages.echoverse.core.domain.model.PersonalityProfile
import model.ChatParticipant

/**
 * A contract for an AI-powered chat analysis engine.
 * This decouples the domain layer from the specific AI framework (e.g., Koog).
 */
interface ChatAnalyzer {

    /**
     * Analyzes a raw chat log and extracts the names of the participants.
     *
     * @param chatContent The full text content of the imported chat.
     * @return A list of unique participants found in the chat.
     */
    suspend fun extractParticipants(chatContent: String): List<ChatParticipant>

    /**
     * Performs a deep analysis of a chat log to generate a personality profile
     * for a specific participant.
     *
     * @param chatContent The full text content of the imported chat.
     * @param participantName The name of the person to create the profile for.
     * @param sourceChatFileName The name of the file being analyzed.
     * @return A PersonalityProfile object containing the AI-generated summary.
     */
    suspend fun synthesizeProfile(
        chatContent: String,
        participantName: String,
        sourceChatFileName: String
    ): PersonalityProfile
}