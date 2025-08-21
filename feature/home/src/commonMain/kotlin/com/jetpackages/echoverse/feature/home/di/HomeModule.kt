package com.jetpackages.echoverse.feature.home.di

import org.koin.dsl.module
import usecase.CreateEchoUseCase
import usecase.DeleteEchoUseCase
import usecase.ExtractParticipantsUseCase
import usecase.GenerateReplyUseCase
import usecase.GetEchoWithProfileUseCase
import usecase.GetEchosUseCase
import usecase.GetMessageHistoryUseCase
import usecase.RetrieveMemoriesUseCase
import usecase.SaveMemoryUseCase
import usecase.SaveMessageUseCase

val homeModule = module {
    factory { GetEchosUseCase(get()) }
    factory { CreateEchoUseCase(get(), get()) }
    factory { DeleteEchoUseCase(get()) }
    factory { ExtractParticipantsUseCase(get()) }
    factory { GetEchoWithProfileUseCase(get()) }
    factory { GenerateReplyUseCase(get()) }
    factory { GetMessageHistoryUseCase(get()) }
    factory { SaveMessageUseCase(get()) }
    factory { SaveMemoryUseCase(get()) }
    factory { RetrieveMemoriesUseCase(get()) }
}