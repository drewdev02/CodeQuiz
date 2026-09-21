package com.code.Quiz.feature.streak.domain.model

/**
 * Domain entity representing the streak status of a single day in the current week.
 */
data class WeekDayStatus(
    val dayOfWeek: String, // "L", "M", "M", "J", "V", "S", "D"
    val dayOfMonth: Int,
    val date: String, // "YYYY-MM-DD"
    val isCompleted: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)
