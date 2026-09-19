package com.code.Quiz.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.code.Quiz.core.database.CodeQuizDatabase
import com.code.Quiz.core.preferences.UserPreferencesRepository
import com.code.Quiz.core.preferences.UserPreferencesRepositoryImpl
import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.navigation.AppNavViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codequiz_user_prefs")

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single<DataStore<Preferences>> { androidContext().dataStore }
    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            dataStore = get(),
            dispatcherProvider = get()
        )
    }

    single<CodeQuizDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CodeQuizDatabase::class.java,
            "codequiz.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    single { get<CodeQuizDatabase>().courseDao() }
    single { get<CodeQuizDatabase>().courseLevelDao() }

    viewModelOf(::AppNavViewModel)
}
