package com.code.Quiz.feature.profile.domain.model

import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus

/**
 * Domain model aggregating overall user progress, streak metrics, XP, and activity history.
 */
data class UserProgressStats(
    val streak: Streak,
    val totalXp: Int,
    val totalExercisesCompleted: Int,
    val completedLevelsCount: Int,
    val weekStreak: List<WeekDayStatus>,
    val activeDatesThisMonth: Set<String>
)
