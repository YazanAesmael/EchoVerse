package usecase

import repository.MessageRepository

/**
 * Retrieves the message history for a specific Echo.
 */
class GetMessageHistoryUseCase(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(echoId: String) = messageRepository.getHistoryForEcho(echoId)
}