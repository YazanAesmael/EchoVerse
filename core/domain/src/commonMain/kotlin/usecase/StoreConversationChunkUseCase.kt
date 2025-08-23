package usecase

import repository.ConversationArchive

class StoreConversationChunkUseCase(private val archive: ConversationArchive) {
    suspend operator fun invoke(echoId: String, chunk: String) = archive.store(echoId, chunk)
}