package com.code.Quiz

import android.app.Application
import com.code.Quiz.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CodeQuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CodeQuizApplication)
            modules(appModules)
        }
    }
}
