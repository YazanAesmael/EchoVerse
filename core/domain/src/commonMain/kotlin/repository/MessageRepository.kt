package repository

import kotlinx.coroutines.flow.Flow
import model.Message

/**
 * A contract for managing the storage and retrieval of chat messages.
 */
interface MessageRepository {

    /**
     * Retrieves a live-updating stream of all messages for a specific Echo.
     */
    fun getHistoryForEcho(echoId: String): Flow<List<Message>>

    /**
     * Saves a new message to the chat history.
     */
    suspend fun saveMessage(echoId: String, message: Message)
}