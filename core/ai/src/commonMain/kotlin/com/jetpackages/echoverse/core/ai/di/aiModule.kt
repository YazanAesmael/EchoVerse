package com.jetpackages.echoverse.core.ai.di

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.jetpackages.echoverse.core.ai.KoogChatAnalyzer
import com.jetpackages.echoverse.core.ai.KoogChatRepository
import com.jetpackages.echoverse.core.ai.cognitive.EmotionalAnalyzerTool
import com.jetpackages.echoverse.core.ai.cognitive.StyleAdvisorTool
import com.jetpackages.echoverse.core.ai.embedder.EchoEmbedder
import org.koin.dsl.module
import repository.ChatAnalyzer
import repository.ChatRepository

const val GEMINI_API_KEY = "AIzaSyDiAuLubCNOg6xMxDiFvlCMvRUopSkhmDg"
const val OPEN_AI_API_KEY = "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"

val aiModule = module {
    val googleExecutor = simpleGoogleAIExecutor(GEMINI_API_KEY)

    factory { EmotionalAnalyzerTool(geminiExecutor = googleExecutor) }
    factory { StyleAdvisorTool(geminiExecutor = googleExecutor) }

    single<ChatAnalyzer> { KoogChatAnalyzer(geminiExecutor = googleExecutor) }
    single<ChatRepository> { KoogChatRepository(geminiExecutor = googleExecutor, get()) }
}