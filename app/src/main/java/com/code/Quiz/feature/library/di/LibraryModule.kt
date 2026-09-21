package com.code.Quiz.feature.library.di

import com.code.Quiz.feature.library.data.repository.LibraryRepositoryImpl
import com.code.Quiz.feature.library.domain.repository.LibraryRepository
import com.code.Quiz.feature.library.ui.ArticleDetailViewModel
import com.code.Quiz.feature.library.ui.LibraryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin dependency injection module for the Library feature.
 */
val libraryModule = module {
    single<LibraryRepository> {
        LibraryRepositoryImpl(
            articleDao = get(),
            databaseSeeder = get(),
            dispatcherProvider = get()
        )
    }

    viewModelOf(::LibraryViewModel)
    viewModelOf(::ArticleDetailViewModel)
}
