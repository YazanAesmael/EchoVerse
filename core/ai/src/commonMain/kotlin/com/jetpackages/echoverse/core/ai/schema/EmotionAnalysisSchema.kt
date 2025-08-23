package com.jetpackages.echoverse.core.ai.schema

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("EmotionAnalysis")
@LLMDescription("An analysis of the immediate emotional context of a conversation.")
data class EmotionAnalysisSchema(
    @property:LLMDescription("A concise description of the user's current mood, e.g., 'Casual and Inquisitive', 'Upset', 'Excited'.")
    val mood: String,

    @property:LLMDescription("The perceived urgency of the user's message, categorized as 'Low', 'Medium', or 'High'.")
    val urgency: String
)

@Serializable
@SerialName("StyleAdvice")
@LLMDescription("A set of stylistic guidelines for how an AI should formulate its next reply.")
data class StyleAdviceSchema(
    @property:LLMDescription("A command describing the ideal message length for this specific reply, e.g., 'Keep the response very short (1-5 words).'")
    val length: String,

    @property:LLMDescription("A floating-point number between 0.0 and 1.0 representing the probability that the person would use an emoji in this specific reply.")
    val emojiProbability: Float,

    @property:LLMDescription("A concise description of the ideal tone for this reply, e.g., 'Playful but brief', 'Warm and supportive'.")
    val tone: String
)