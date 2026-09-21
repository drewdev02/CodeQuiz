package com.code.Quiz.feature.streak.di

import com.code.Quiz.feature.streak.data.repository.StreakRepositoryImpl
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import com.code.Quiz.feature.streak.domain.usecase.GetStreakUseCase
import com.code.Quiz.feature.streak.domain.usecase.GetWeekStreakUseCase
import com.code.Quiz.feature.streak.domain.usecase.RecordDailyActivityUseCase
import org.koin.dsl.module

/**
 * Koin module for the Streak gamification feature.
 */
val streakModule = module {
    single<StreakRepository> {
        StreakRepositoryImpl(
            streakDao = get(),
            dispatcherProvider = get()
        )
    }

    factory { GetStreakUseCase(streakRepository = get()) }
    factory { RecordDailyActivityUseCase(streakRepository = get()) }
    factory { GetWeekStreakUseCase(streakRepository = get()) }
}
