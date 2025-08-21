package model

import kotlin.jvm.JvmInline

/**
 * Represents a single participant extracted from an imported chat log.
 *
 * @property name The name of the participant as it appears in the chat file.
 */
@JvmInline
value class ChatParticipant(val name: String)