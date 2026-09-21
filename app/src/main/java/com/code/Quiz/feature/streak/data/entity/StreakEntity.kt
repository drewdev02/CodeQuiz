package com.code.Quiz.feature.streak.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing the persisted user streak state.
 */
@Entity(tableName = "user_streak")
data class StreakEntity(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int,
    val maxStreak: Int,
    val lastActiveDate: String // "YYYY-MM-DD"
)
