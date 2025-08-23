package usecase

import model.PersonalityProfile
import model.Message
import repository.ChatRepository

/**
 * Generates a reply from an Echo.
 */
class GenerateReplyUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        profile: PersonalityProfile,
        history: List<Message>,
        userMessage: String
    ) = chatRepository.getReply(profile, history, userMessage)
}