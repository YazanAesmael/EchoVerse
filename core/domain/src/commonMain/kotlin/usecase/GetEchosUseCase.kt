package usecase

import repository.EchoRepository

/**
 * Retrieves a live-updating list of all user-created Echos.
 */
class GetEchosUseCase(
    private val echoRepository: EchoRepository
) {
    operator fun invoke() = echoRepository.getAllEchos()
}