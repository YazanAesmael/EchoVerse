package com.jetpackages.echoverse.core.ai.di

import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.jetpackages.echoverse.core.ai.KoogChatAnalyzer
import com.jetpackages.echoverse.core.ai.KoogChatRepository
import org.koin.dsl.module
import repository.ChatAnalyzer
import repository.ChatRepository

const val GEMINI_API_KEY = "AIzaSyAvYQBwq3xHqbzCD7ineZHW5xRchmJrFk0"

val aiModule = module {
    single<ChatAnalyzer> { KoogChatAnalyzer(geminiExecutor = simpleGoogleAIExecutor(GEMINI_API_KEY)) }
    single<ChatRepository> { KoogChatRepository(geminiExecutor = simpleGoogleAIExecutor(GEMINI_API_KEY), get()) }
}