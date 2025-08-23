package repository

/**
 * A contract for managing an Echo's long-term, searchable conversation history.
 */
interface ConversationArchive {
    suspend fun store(echoId: String, conversationChunk: String)
    suspend fun retrieveAll(echoId: String): List<String>
}