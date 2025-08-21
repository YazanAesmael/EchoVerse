package usecase

import model.Message
import repository.MessageRepository

/**
 * Saves a message to an Echo's chat history.
 */
class SaveMessageUseCase(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(echoId: String, message: Message) {
        messageRepository.saveMessage(echoId, message)
    }
}