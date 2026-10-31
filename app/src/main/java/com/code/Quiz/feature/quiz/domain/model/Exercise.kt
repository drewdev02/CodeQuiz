package com.code.Quiz.feature.quiz.domain.model

/**
 * Domain entity representing an interactive micro-challenge / exercise.
 * Pure Kotlin model without framework dependencies.
 */
data class Exercise(
    val id: String,
    val levelId: String,
    val question: String,
    val codeSnippet: String? = null,
    val language: String = "Kotlin",
    val options: List<ExerciseOption>,
    val explanation: String,
    val difficulty: ExerciseDifficulty = ExerciseDifficulty.EASY
)
