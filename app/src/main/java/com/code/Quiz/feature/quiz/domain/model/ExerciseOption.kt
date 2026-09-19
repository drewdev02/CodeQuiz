package com.code.Quiz.feature.quiz.domain.model

/**
 * Domain entity representing an option for an exercise.
 * Pure Kotlin model without framework dependencies.
 */
data class ExerciseOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean
)
