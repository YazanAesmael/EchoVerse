package usecase

import repository.EchoRepository


/**
 * Deletes a specific Echo from the system.
 */
class DeleteEchoUseCase(
    private val echoRepository: EchoRepository
) {
    suspend operator fun invoke(echoId: String) {
        echoRepository.deleteEcho(echoId)
    }
}