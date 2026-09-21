package com.code.Quiz.feature.streak.domain.model

/**
 * Domain entity representing daily learning activity stats.
 */
data class DailyActivity(
    val date: String, // "YYYY-MM-DD"
    val xpEarned: Int,
    val exercisesCompleted: Int
)
