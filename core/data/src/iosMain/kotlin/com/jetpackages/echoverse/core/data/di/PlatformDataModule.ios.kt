package com.jetpackages.echoverse.core.data.di

import com.jetpackages.echoverse.core.data.database.DatabaseDriverFactory
import org.koin.dsl.module

// This module will only be used in the iosApp
actual val platformDataModule = module {
    single { DatabaseDriverFactory() }
}