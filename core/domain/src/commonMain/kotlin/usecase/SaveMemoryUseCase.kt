package usecase

import repository.MemoryRepository

class SaveMemoryUseCase(private val memoryRepository: MemoryRepository) {
    suspend operator fun invoke(echoId: String, content: String) =
        memoryRepository.saveMemoryFragment(echoId, content)
}