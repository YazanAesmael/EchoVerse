package com.jetpackages.echoverse.core.ai.cognitive

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolResult
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonStructuredData
import com.jetpackages.echoverse.core.ai.schema.StyleAdviceSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import model.PersonalityProfile
import model.StyleAdvice


/**
 * A class-based tool that generates stylistic advice based on a personality profile.
 */
class StyleAdvisorTool(
    private val geminiExecutor: PromptExecutor
) : Tool<StyleAdvisorTool.Args, StyleAdvisorTool.Result>() {

    @Serializable
    data class Args(val profile: PersonalityProfile) : ToolArgs

    @Serializable
    data class Result(
        val serializable: StyleAdvice
    ) : ToolResult.JSONSerializable<Result> {
        override fun getSerializer(): KSerializer<Result> = serializer()
    }

    override val argsSerializer: KSerializer<Args> = Args.serializer()
    override val descriptor: ToolDescriptor = ToolDescriptor(name = "style_advisor", description = "Generates stylistic advice.")
    private val schema = JsonStructuredData.createJsonStructure<StyleAdviceSchema>()

    override suspend fun execute(args: Args): Result {
        val advice = advise(args.profile)
        return Result(advice)
    }

    suspend fun advise(profile: PersonalityProfile): StyleAdvice {
        // --- THIS IS THE CRITICAL AND FINAL PROMPT FIX ---
        val systemPrompt = """
            You are a 'Style Advisor' AI for a digital twin chatbot. Your SOLE function is to
            analyze the provided PERSONA CONFIGURATION and generate a JSON object containing
            stylistic advice for the chatbot's NEXT reply.

            You MUST base your advice STRICTLY on the TENDENCIES described in the
            configuration. Your goal is to create natural, human-like variation.

            **Probability Guide for 'emojiProbability':**
            - If the persona 'rarely' uses emojis, the probability should be very low (0.05 - 0.15).
            - If the persona 'occasionally' uses emojis, the probability should be moderate (0.2 - 0.4).
            - If the persona 'frequently' uses emojis, the probability should be high, but NOT guaranteed (0.5 - 0.7).
            - NEVER return a probability of 1.0.

            Your output must be a JSON object and nothing else.
        """.trimIndent()

        // We now provide the full, rich profile as the user prompt.
        val userPrompt = """
            PERSONA CONFIGURATION:
            - Relationship to User: ${profile.coreIdentity.relationshipToUser}
            - Primary Tone: ${profile.communicationRules.tone}
            - Length Tendency: ${profile.communicationRules.averageMessageLength}
            - Emoji Frequency Tendency: ${profile.communicationRules.emojiFrequency}
            - Top Emojis: ${profile.lexicon.topEmojis.joinToString(" ")}
        """.trimIndent()
        // --------------------------------------------------

        val result = geminiExecutor.executeStructured(
            prompt = prompt("style-advisor-prompt") {
                system(systemPrompt)
                user(userPrompt)
            },
            structure = schema,
            mainModel = GoogleModels.Gemini1_5FlashLatest
        ).getOrNull()?.structure

        return if (result != null) {
            StyleAdvice(
                length = result.length,
                emojiProbability = result.emojiProbability.coerceIn(0.0f, 1.0f),
                tone = result.tone
            )
        } else {
            StyleAdvice("Average", 0.5f, "Neutral")
        }
    }
}
