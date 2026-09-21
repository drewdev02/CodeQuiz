package com.code.Quiz.feature.profile.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import com.code.Quiz.feature.profile.domain.repository.ProfileRepository
import com.code.Quiz.feature.streak.data.dao.StreakDao
import com.code.Quiz.feature.streak.domain.model.DailyActivity
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.util.Locale

/**
 * Implementation of [ProfileRepository] aggregating streak, course levels, and activity data.
 */
class ProfileRepositoryImpl(
    private val streakRepository: StreakRepository,
    private val streakDao: StreakDao,
    private val courseLevelDao: CourseLevelDao,
    private val dispatcherProvider: DispatcherProvider
) : ProfileRepository {

    override fun getUserStats(): Flow<UserProgressStats> {
        return combine(
            streakRepository.getStreak(),
            streakDao.getTotalXp(),
            streakDao.getTotalExercisesCompleted(),
            courseLevelDao.getCompletedLevelsCount(),
            streakRepository.getWeekStreak(),
            streakRepository.getRecentActivities(35)
        ) { array ->
            val streak = array[0] as Streak
            val totalXp = array[1] as Int
            val totalExercises = array[2] as Int
            val completedLevels = array[3] as Int
            @Suppress("UNCHECKED_CAST")
            val weekStreak = array[4] as List<WeekDayStatus>
            @Suppress("UNCHECKED_CAST")
            val recentActivities = array[5] as List<DailyActivity>

            val today = LocalDate.now()
            val monthPrefix = String.format(Locale.ROOT, "%04d-%02d", today.year, today.monthValue)
            val activeDates = recentActivities
                .filter { it.date.startsWith(monthPrefix) && (it.exercisesCompleted > 0 || it.xpEarned > 0) }
                .map { it.date }
                .toMutableSet()

            if (streak.isActiveToday) {
                activeDates.add(today.toString())
            }

            UserProgressStats(
                streak = streak,
                totalXp = totalXp,
                totalExercisesCompleted = totalExercises,
                completedLevelsCount = completedLevels,
                weekStreak = weekStreak,
                activeDatesThisMonth = activeDates
            )
        }.flowOn(dispatcherProvider.io)
    }
}
