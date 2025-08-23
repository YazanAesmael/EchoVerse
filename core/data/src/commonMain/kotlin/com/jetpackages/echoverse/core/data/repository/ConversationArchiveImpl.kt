package com.jetpackages.echoverse.core.data.repository

import com.jetpackages.echoverse.db.EchoVerseDatabase
import kotlinx.datetime.Clock
import repository.ConversationArchive

class ConversationArchiveImpl(
    private val db: EchoVerseDatabase
) : ConversationArchive {

    private val queries = db.conversationArchiveEntityQueries

    override suspend fun store(echoId: String, conversationChunk: String) {
        queries.insert(
            echoId = echoId,
            chunk = conversationChunk,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun retrieveAll(echoId: String): List<String> {
        return queries.getAllForEcho(echoId).executeAsList()
    }
}