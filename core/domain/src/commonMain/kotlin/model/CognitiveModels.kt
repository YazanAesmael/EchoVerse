package model

import kotlinx.serialization.Serializable

/**
 * Represents the immediate emotional context of a conversation turn.
 */
@Serializable
data class EmotionAnalysis(
    val mood: String, // e.g., "Casual and Inquisitive", "Serious", "Excited"
    val urgency: String // e.g., "Low", "Medium", "High"
)

/**
 * Represents the stylistic "advice" generated for a single reply.
 * This is the "weights" system.
 */
@Serializable
data class StyleAdvice(
    val length: String, // e.g., "Short, 1-5 words"
    val emojiProbability: Float, // A value between 0.0 and 1.0
    val tone: String // e.g., "Playful but brief"
)