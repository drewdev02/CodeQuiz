package com.code.Quiz.feature.quiz.ui

import com.code.Quiz.feature.quiz.domain.model.Exercise

/**
 * Sealed interface representing all possible states for the Quiz/Level screen.
 */
sealed interface QuizUiState {
    data object Loading : QuizUiState

    data class Content(
        val levelId: String,
        val levelTitle: String,
        val exercises: List<Exercise>,
        val currentIndex: Int,
        val selectedOptionId: String? = null,
        val isSubmitted: Boolean = false,
        val isCorrect: Boolean = false,
        val score: Int = 0
    ) : QuizUiState

    data class Completed(
        val levelId: String,
        val levelTitle: String,
        val totalQuestions: Int,
        val correctAnswers: Int,
        val xpEarned: Int
    ) : QuizUiState

    data class Error(val message: String) : QuizUiState
}
