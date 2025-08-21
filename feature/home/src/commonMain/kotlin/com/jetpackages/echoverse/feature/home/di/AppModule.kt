package com.jetpackages.echoverse.feature.home.di

import com.jetpackages.echoverse.core.ai.di.aiModule
import com.jetpackages.echoverse.core.data.di.dataModule
import com.jetpackages.echoverse.core.data.di.platformDataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            platformDataModule,
            dataModule,
            aiModule,
            homeModule
        )
    }
}