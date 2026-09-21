package com.code.Quiz.feature.streak.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.streak.data.dao.StreakDao
import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import com.code.Quiz.feature.streak.data.mapper.toDomain
import com.code.Quiz.feature.streak.domain.model.DailyActivity
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Implementation of [StreakRepository] interacting with Room database [StreakDao].
 */
class StreakRepositoryImpl(
    private val streakDao: StreakDao,
    private val dispatcherProvider: DispatcherProvider
) : StreakRepository {

    override fun getStreak(): Flow<Streak> = streakDao.getStreakFlow().map { entity ->
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        if (entity == null) {
            Streak(
                currentStreak = 0,
                maxStreak = 0,
                lastActiveDate = null,
                isActiveToday = false
            )
        } else {
            val lastDate = parseDateOrNull(entity.lastActiveDate)
            when {
                lastDate == today -> entity.toDomain(
                    isActiveToday = true,
                    effectiveStreak = entity.currentStreak
                )
                lastDate == yesterday -> entity.toDomain(
                    isActiveToday = false,
                    effectiveStreak = entity.currentStreak
                )
                else -> Streak(
                    currentStreak = 0,
                    maxStreak = entity.maxStreak,
                    lastActiveDate = entity.lastActiveDate,
                    isActiveToday = false
                )
            }
        }
    }.flowOn(dispatcherProvider.io)

    override fun getRecentActivities(limit: Int): Flow<List<DailyActivity>> =
        streakDao.getRecentActivities(limit).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(dispatcherProvider.io)

    override fun getWeekStreak(): Flow<List<WeekDayStatus>> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)
        val dayLabels = listOf("L", "M", "M", "J", "V", "S", "D")

        return streakDao.getActivitiesBetween(monday.toString(), sunday.toString()).map { activities ->
            val activeDates = activities
                .filter { it.exercisesCompleted > 0 || it.xpEarned > 0 }
                .map { it.date }
                .toSet()

            (0..6).map { dayIndex ->
                val date = monday.plusDays(dayIndex.toLong())
                val dateStr = date.toString()
                WeekDayStatus(
                    dayOfWeek = dayLabels[dayIndex],
                    dayOfMonth = date.dayOfMonth,
                    date = dateStr,
                    isCompleted = activeDates.contains(dateStr),
                    isToday = date == today,
                    isFuture = date.isAfter(today)
                )
            }
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun recordActivity(xpEarned: Int, exercisesCount: Int) {
        withContext(dispatcherProvider.io) {
            val today = LocalDate.now()
            val todayStr = today.toString()
            val yesterday = today.minusDays(1)

            // 1. Update or insert daily activity for today
            val existingActivity = streakDao.getActivityByDate(todayStr)
            val updatedActivity = if (existingActivity != null) {
                existingActivity.copy(
                    xpEarned = existingActivity.xpEarned + xpEarned,
                    exercisesCompleted = existingActivity.exercisesCompleted + exercisesCount
                )
            } else {
                DailyActivityEntity(
                    date = todayStr,
                    xpEarned = xpEarned,
                    exercisesCompleted = exercisesCount
                )
            }
            streakDao.insertOrUpdateActivity(updatedActivity)

            // 2. Update Streak entity
            val currentEntity = streakDao.getStreak()
            val lastDate = parseDateOrNull(currentEntity?.lastActiveDate)

            val (newCurrentStreak, newMaxStreak) = when {
                currentEntity == null || lastDate == null -> {
                    1 to 1
                }
                lastDate == today -> {
                    currentEntity.currentStreak to currentEntity.maxStreak
                }
                lastDate == yesterday -> {
                    val nextStreak = currentEntity.currentStreak + 1
                    nextStreak to maxOf(nextStreak, currentEntity.maxStreak)
                }
                else -> {
                    // lastDate < yesterday (streak broken)
                    1 to maxOf(1, currentEntity.maxStreak)
                }
            }

            streakDao.insertOrUpdateStreak(
                StreakEntity(
                    id = 1,
                    currentStreak = newCurrentStreak,
                    maxStreak = newMaxStreak,
                    lastActiveDate = todayStr
                )
            )
        }
    }

    private fun parseDateOrNull(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            LocalDate.parse(dateStr)
        } catch (_: Exception) {
            null
        }
    }
}
