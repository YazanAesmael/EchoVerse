package com.jetpackages.echoverse.core.data.repository

import com.jetpackages.echoverse.db.EchoVerseDatabase
import kotlinx.datetime.Clock
import repository.MemoryRepository

class MemoryRepositoryImpl(
    private val db: EchoVerseDatabase
) : MemoryRepository {

    private val queries = db.memoryFragmentEntityQueries

    override suspend fun saveMemoryFragment(echoId: String, content: String) {
        queries.insert(
            echoId = echoId,
            content = content,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun retrieveAllMemories(echoId: String): List<String> {
        return queries.getAllForEcho(echoId)
            .executeAsList()
            .map { it.content }
    }
}