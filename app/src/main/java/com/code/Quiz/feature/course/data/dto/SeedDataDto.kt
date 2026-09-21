package com.code.Quiz.feature.course.data.dto

import com.code.Quiz.feature.library.data.dto.ArticleDto
import kotlinx.serialization.Serializable

/**
 * Root DTO mapping the bundled JSON seed data catalog.
 */
@Serializable
data class SeedDataDto(
    val version: Int = 1,
    val courses: List<CourseSeedDto>,
    val library: List<ArticleDto> = emptyList()
)

/**
 * DTO representing an individual course within the seed catalog.
 */
@Serializable
data class CourseSeedDto(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val badge: String? = null,
    val iconType: String = "ROCKET",
    val isUnlocked: Boolean = true,
    val levels: List<CourseLevelSeedDto> = emptyList()
)

/**
 * DTO representing a level inside a course within the seed catalog.
 */
@Serializable
data class CourseLevelSeedDto(
    val id: String,
    val levelNumber: Int,
    val title: String,
    val description: String,
    val status: String = "LOCKED",
    val totalExercises: Int = 5,
    val xpReward: Int = 30,
    val exercises: List<ExerciseSeedDto> = emptyList()
)

/**
 * DTO representing an exercise micro-challenge inside a level in seed data.
 */
@Serializable
data class ExerciseSeedDto(
    val id: String,
    val question: String,
    val codeSnippet: String? = null,
    val language: String = "Kotlin",
    val options: List<ExerciseOptionSeedDto> = emptyList(),
    val explanation: String
)

/**
 * DTO representing an option inside an exercise in seed data.
 */
@Serializable
data class ExerciseOptionSeedDto(
    val id: String,
    val text: String,
    val isCorrect: Boolean
)
