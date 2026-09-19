package com.code.Quiz.feature.course.domain.model

/**
 * Represents the progression state of an individual course level.
 * Pure Kotlin domain model decoupled from framework dependencies.
 */
enum class LevelStatus {
    COMPLETED,
    ACTIVE,
    LOCKED
}

/**
 * Domain model representing a gamified level within a course roadmap.
 * Contains metadata, difficulty stats, reward points, and completion status.
 */
data class CourseLevel(
    val id: String,
    val courseId: String,
    val levelNumber: Int,
    val title: String,
    val description: String,
    val status: LevelStatus,
    val totalExercises: Int,
    val xpReward: Int
)
