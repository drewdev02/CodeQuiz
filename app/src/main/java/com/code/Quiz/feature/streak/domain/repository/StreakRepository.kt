package com.code.Quiz.feature.streak.domain.repository

import com.code.Quiz.feature.streak.domain.model.DailyActivity
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for managing streak state, activity logging, and weekly progress.
 */
interface StreakRepository {
    fun getStreak(): Flow<Streak>
    fun getRecentActivities(limit: Int = 35): Flow<List<DailyActivity>>
    fun getWeekStreak(): Flow<List<WeekDayStatus>>
    suspend fun recordActivity(xpEarned: Int, exercisesCount: Int)
}
