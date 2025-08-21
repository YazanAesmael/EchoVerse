package repository

/**
 * A contract for managing an Echo's long-term, episodic memory.
 */
interface MemoryRepository {
    suspend fun saveMemoryFragment(echoId: String, content: String)
    suspend fun retrieveAllMemories(echoId: String): List<String>
}