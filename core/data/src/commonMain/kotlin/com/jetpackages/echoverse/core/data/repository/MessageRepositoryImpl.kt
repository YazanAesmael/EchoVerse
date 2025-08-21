package com.jetpackages.echoverse.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jetpackages.echoverse.core.data.mappers.toMessage
import com.jetpackages.echoverse.db.EchoVerseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.Message
import repository.MessageRepository

class MessageRepositoryImpl(
    private val db: EchoVerseDatabase
) : MessageRepository {

    private val queries = db.messageEntityQueries

    override fun getHistoryForEcho(echoId: String): Flow<List<Message>> {
        return queries.getHistoryForEcho(echoId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { messageEntityList ->
                messageEntityList.map { it.toMessage() }
            }
    }

    override suspend fun saveMessage(echoId: String, message: Message) {
        queries.insert(
            echoId = echoId,
            text = message.text,
            isFromUser = message.isFromUser,
            timestamp = message.timestamp
        )
    }
}