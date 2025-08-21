package com.jetpackages.echoverse.core.ai.schema

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("StructuredPersonalityProfile")
@LLMDescription("A detailed, structured analysis of a person's communication style from a chat log.")
data class StructuredProfileSchema(
    @property:LLMDescription("The core identity of the person being analyzed.")
    val coreIdentity: CoreIdentitySchema,

    @property:LLMDescription("Specific, observable rules about how the person communicates.")
    val communicationRules: CommunicationRulesSchema,

    @property:LLMDescription("A collection of verbatim words, phrases, and emojis the person frequently uses.")
    val lexicon: LexiconSchema,
)

@Serializable
@SerialName("CoreIdentity")
data class CoreIdentitySchema(
    @property:LLMDescription("The name of the person being analyzed.")
    val name: String,
    
    @property:LLMDescription("The analyzed relationship to the other chat participant (e.g., 'Loving partner', 'Close friend').")
    val relationshipToUser: String,
    
    @property:LLMDescription("A brief summary of the person's daily life or key characteristics mentioned in the chat.")
    val dailyLifeSummary: String
)

@Serializable
@SerialName("CommunicationRules")
data class CommunicationRulesSchema(
    @property:LLMDescription("A direct command describing the average message length (e.g., 'ALWAYS keep responses short, 1-10 words.').")
    val averageMessageLength: String,

    @property:LLMDescription("A direct command describing the primary tone (e.g., 'DO be affectionate and playful.').")
    val tone: String,

    @property:LLMDescription("A direct command describing the capitalization style (e.g., 'NEVER use capital letters.').")
    val capitalizationStyle: String,

    @property:LLMDescription("A direct command describing the punctuation style (e.g., 'Rarely use periods, prefer emojis.').")
    val punctuationStyle: String,

    @property:LLMDescription("A direct command describing how often emojis are used (e.g., 'Use emojis in almost every message.').")
    val emojiFrequency: String
)

@Serializable
@SerialName("Lexicon")
data class LexiconSchema(
    @property:LLMDescription("A list of 3-5 verbatim and quoted signature phrases or slang terms.")
    val signaturePhrases: List<String>,

    @property:LLMDescription("A list of 3-5 verbatim and quoted terms of endearment or pet names.")
    val termsOfEndearment: List<String>,

    @property:LLMDescription("A list of the top 5-7 most frequently used emojis.")
    val topEmojis: List<String>
)