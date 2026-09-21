package com.code.Quiz.feature.profile.di

import com.code.Quiz.feature.profile.data.repository.ProfileRepositoryImpl
import com.code.Quiz.feature.profile.domain.repository.ProfileRepository
import com.code.Quiz.feature.profile.ui.ProgressViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for the Profile & Progress tracking feature.
 */
val profileModule = module {
    single<ProfileRepository> {
        ProfileRepositoryImpl(
            streakRepository = get(),
            streakDao = get(),
            courseLevelDao = get(),
            dispatcherProvider = get()
        )
    }

    viewModelOf(::ProgressViewModel)
}
