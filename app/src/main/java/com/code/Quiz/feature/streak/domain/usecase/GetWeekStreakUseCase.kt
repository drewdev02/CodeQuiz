package com.code.Quiz.feature.streak.domain.usecase

import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe the weekly streak status for each day (Monday to Sunday).
 */
class GetWeekStreakUseCase(
    private val streakRepository: StreakRepository
) {
    operator fun invoke(): Flow<List<WeekDayStatus>> = streakRepository.getWeekStreak()
}
