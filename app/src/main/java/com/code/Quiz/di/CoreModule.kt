package com.code.Quiz.di

import androidx.room.Room
import com.code.Quiz.core.database.CodeQuizDatabase
import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.core.util.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single<CodeQuizDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CodeQuizDatabase::class.java,
            "codequiz.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    single { get<CodeQuizDatabase>().courseDao() }
    single { get<CodeQuizDatabase>().courseLevelDao() }
}
