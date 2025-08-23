package usecase

import repository.ChatAnalyzer
import repository.ConversationArchive
import repository.EchoRepository

/**
 * Encapsulates the entire business logic for creating a new Echo from a chat log.
 * It orchestrates the analysis and storage of the new Echo.
 */
class CreateEchoUseCase(
    private val chatAnalyzer: ChatAnalyzer,
    private val echoRepository: EchoRepository,
    private val conversationArchive: ConversationArchive
) {
    suspend operator fun invoke(
        chatContent: String,
        participantToEcho: String,
        echoName: String,
        profilePictureUri: String?,
        sourceChatFileName: String
    ) {
        // 1. Synthesize the static "DNA" profile.
        val personalityProfile = chatAnalyzer.synthesizeProfile(
            chatContent = chatContent,
            participantName = participantToEcho,
            sourceChatFileName = sourceChatFileName
        )

        // 2. Create the Echo to get its unique ID.
        val newEcho = echoRepository.createEcho(
            name = echoName,
            profilePictureUri = profilePictureUri,
            personalityProfile = personalityProfile
        )

        // 3. Ingest the entire chat log into the long-term archive.
        chatContent.chunked(1000).forEach { chunk ->
            conversationArchive.store(newEcho.id, chunk)
        }
    }
}