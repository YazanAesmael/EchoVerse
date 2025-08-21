package model

/**
 * Represents a single message in a chat conversation.
 *
 * @property text The content of the message.
 * @property isFromUser True if the message was sent by the user, false if by the Echo.
 * @property timestamp The time the message was sent.
 */
data class Message(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long
)