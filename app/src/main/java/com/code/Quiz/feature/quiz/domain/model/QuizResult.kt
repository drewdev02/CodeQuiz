package com.code.Quiz.feature.quiz.domain.model

/**
 * Domain entity representing the summary result of a completed level quiz.
 * Pure Kotlin model without framework dependencies.
 */
data class QuizResult(
    val levelId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val xpEarned: Int
)
