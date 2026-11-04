package com.code.Quiz

import android.app.Application
import com.code.Quiz.di.appModules
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CodeQuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val koinApp = startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CodeQuizApplication)
            modules(appModules)
        }

        val seeder = koinApp.koin.get<DatabaseSeeder>()
        CoroutineScope(Dispatchers.IO).launch {
            seeder.seedIfNeeded()
        }
    }
}
