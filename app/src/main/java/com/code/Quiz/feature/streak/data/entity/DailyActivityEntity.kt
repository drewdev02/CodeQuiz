package com.code.Quiz.feature.streak.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing logged daily learning activity.
 */
@Entity(tableName = "daily_activities")
data class DailyActivityEntity(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val xpEarned: Int,
    val exercisesCompleted: Int
)
