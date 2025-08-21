package usecase

import repository.EchoRepository

/**
 * Retrieves a single Echo and its associated personality profile by its ID.
 */
class GetEchoWithProfileUseCase(
    private val echoRepository: EchoRepository
) {
    suspend operator fun invoke(id: String) = echoRepository.getEchoWithProfile(id)
}