package usecase

import repository.MemoryRepository

class RetrieveMemoriesUseCase(private val memoryRepository: MemoryRepository) {
    suspend operator fun invoke(echoId: String) = memoryRepository.retrieveAllMemories(echoId)
}