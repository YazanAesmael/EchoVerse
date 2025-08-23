package com.jetpackages.echoverse.android

import android.app.Application
import com.jetpackages.echoverse.feature.home.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class EchoVerseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@EchoVerseApplication)
        }
    }
}