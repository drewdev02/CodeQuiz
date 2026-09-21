package com.code.Quiz.feature.streak.domain.usecase

import com.code.Quiz.feature.streak.domain.repository.StreakRepository

/**
 * Use case to record daily practice activity and advance streak.
 */
class RecordDailyActivityUseCase(
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(xpEarned: Int, exercisesCount: Int) {
        streakRepository.recordActivity(xpEarned = xpEarned, exercisesCount = exercisesCount)
    }
}
