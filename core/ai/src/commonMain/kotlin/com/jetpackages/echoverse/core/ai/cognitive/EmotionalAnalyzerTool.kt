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
import com.jetpackages.echoverse.core.ai.schema.EmotionAnalysisSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import model.EmotionAnalysis

/**
 * A class-based tool that analyzes the emotional context of a short conversation snippet.
 */
class EmotionalAnalyzerTool(
    private val geminiExecutor: PromptExecutor
) : Tool<ToolArgs.Empty, EmotionalAnalyzerTool.Result>() {

    // The Result class now correctly implements the recursive generic.
    @Serializable
    data class Result(
        val serializable: EmotionAnalysis
    ) : ToolResult.JSONSerializable<Result> {
        override fun getSerializer(): KSerializer<Result> = serializer()
    }

    override val argsSerializer: KSerializer<ToolArgs.Empty> = ToolArgs.Empty.serializer()
    override val descriptor: ToolDescriptor = ToolDescriptor(name = "emotional_analyzer", description = "Analyzes emotional context.")
    private val schema = JsonStructuredData.createJsonStructure<EmotionAnalysisSchema>()

    override suspend fun execute(args: ToolArgs.Empty): Result {
        val analysis = analyze("")
        return Result(analysis)
    }

    suspend fun analyze(chatSnippet: String): EmotionAnalysis {
        val result = geminiExecutor.executeStructured(
            prompt = prompt("emotion-analysis-prompt") {
                system("You are an expert in conversation analysis...")
                user(chatSnippet)
            },
            structure = schema,
            mainModel = GoogleModels.Gemini1_5FlashLatest
        ).getOrNull()?.structure

        return if (result != null) {
            EmotionAnalysis(mood = result.mood, urgency = result.urgency)
        } else {
            EmotionAnalysis("Neutral", "Low")
        }
    }
}