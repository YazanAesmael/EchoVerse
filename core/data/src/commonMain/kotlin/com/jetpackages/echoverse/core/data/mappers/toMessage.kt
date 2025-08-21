package com.jetpackages.echoverse.core.data.mappers

import com.jetpackages.echoverse.db.MessageEntity
import model.Message

/**
 * Maps a MessageEntity (database model) to a Message (domain model).
 */
fun MessageEntity.toMessage(): Message {
    return Message(
        text = this.text,
        isFromUser = this.isFromUser,
        timestamp = this.timestamp
    )
}