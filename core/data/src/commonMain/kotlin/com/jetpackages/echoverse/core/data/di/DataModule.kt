package com.jetpackages.echoverse.core.data.di

import com.jetpackages.echoverse.core.data.database.createDatabase
import com.jetpackages.echoverse.core.data.repository.EchoRepositoryImpl
import com.jetpackages.echoverse.core.data.repository.MemoryRepositoryImpl
import com.jetpackages.echoverse.core.data.repository.MessageRepositoryImpl
import org.koin.dsl.module
import repository.EchoRepository
import repository.MemoryRepository
import repository.MessageRepository

val dataModule = module {
    single<EchoRepository> { EchoRepositoryImpl(get()) }
    single<MessageRepository> { MessageRepositoryImpl(get()) }
    single<MemoryRepository> { MemoryRepositoryImpl(get()) }
    single { createDatabase(get()) }
}