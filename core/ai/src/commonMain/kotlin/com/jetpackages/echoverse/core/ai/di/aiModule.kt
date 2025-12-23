package com.jetpackages.echoverse.core.ai.di

import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.jetpackages.echoverse.core.ai.KoogChatAnalyzer
import com.jetpackages.echoverse.core.ai.KoogChatRepository
import com.jetpackages.echoverse.core.ai.cognitive.EmotionalAnalyzerTool
import com.jetpackages.echoverse.core.ai.cognitive.StyleAdvisorTool
import org.koin.dsl.module
import repository.ChatAnalyzer
import repository.ChatRepository

const val GEMINI_API_KEY = "GEMINI_API_KEY"

val aiModule = module {
    val executor = simpleGoogleAIExecutor(GEMINI_API_KEY)

    factory { EmotionalAnalyzerTool(geminiExecutor = executor) }
    factory { StyleAdvisorTool(geminiExecutor = executor) }

    single<ChatAnalyzer> { KoogChatAnalyzer(geminiExecutor = executor) }
    single<ChatRepository> { KoogChatRepository(geminiExecutor = executor, get()) }
}
