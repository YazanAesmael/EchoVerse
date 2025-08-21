package com.jetpackages.echoverse.core.ai.schema

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ParticipantList")
@LLMDescription("A list of the unique names of participants found in a chat conversation.")
data class ParticipantListSchema(
    @property:LLMDescription("An array of unique participant names.")
    val participants: List<String>
)