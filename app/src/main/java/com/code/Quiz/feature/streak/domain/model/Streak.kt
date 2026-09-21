package com.code.Quiz.feature.streak.domain.model

/**
 * Domain entity representing the user's gamified streak state.
 */
data class Streak(
    val currentStreak: Int,
    val maxStreak: Int,
    val lastActiveDate: String?, // "YYYY-MM-DD"
    val isActiveToday: Boolean
)
