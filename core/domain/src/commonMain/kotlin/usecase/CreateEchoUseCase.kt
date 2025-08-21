package usecase

import repository.ChatAnalyzer
import repository.EchoRepository

/**
 * Encapsulates the entire business logic for creating a new Echo from a chat log.
 * It orchestrates the analysis and storage of the new Echo.
 */
class CreateEchoUseCase(
    private val chatAnalyzer: ChatAnalyzer,
    private val echoRepository: EchoRepository
) {
    suspend operator fun invoke(
        chatContent: String,
        participantToEcho: String,
        echoName: String,
        profilePictureUri: String?,
        sourceChatFileName: String
    ) {
        // 1. Let the AI synthesize the personality profile
        val personalityProfile = chatAnalyzer.synthesizeProfile(
            chatContent = chatContent,
            participantName = participantToEcho,
            sourceChatFileName = sourceChatFileName
        )

        // 2. Use the repository to create and save the new Echo with the profile
        echoRepository.createEcho(
            name = echoName,
            profilePictureUri = profilePictureUri,
            personalityProfile = personalityProfile
        )
    }
}