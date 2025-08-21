package usecase

import repository.ChatAnalyzer

/**
 * Extracts the list of participants from a raw chat log.
 */
class ExtractParticipantsUseCase(
    private val chatAnalyzer: ChatAnalyzer
) {
    suspend operator fun invoke(chatContent: String) = chatAnalyzer.extractParticipants(chatContent)
}