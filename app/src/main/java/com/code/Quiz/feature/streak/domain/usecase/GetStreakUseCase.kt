package com.code.Quiz.feature.streak.domain.usecase

import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe the current streak state.
 */
class GetStreakUseCase(
    private val streakRepository: StreakRepository
) {
    operator fun invoke(): Flow<Streak> = streakRepository.getStreak()
}
