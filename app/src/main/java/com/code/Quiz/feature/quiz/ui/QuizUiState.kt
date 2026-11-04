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
        val selectedOptionIds: Set<String> = emptySet(),
        val isSubmitted: Boolean = false,
        val isCorrect: Boolean = false,
        val score: Int = 0
    ) : QuizUiState {
        val selectedOptionId: String? get() = selectedOptionIds.firstOrNull()

        constructor(
            levelId: String,
            levelTitle: String,
            exercises: List<Exercise>,
            currentIndex: Int,
            selectedOptionId: String?,
            isSubmitted: Boolean = false,
            isCorrect: Boolean = false,
            score: Int = 0
        ) : this(
            levelId = levelId,
            levelTitle = levelTitle,
            exercises = exercises,
            currentIndex = currentIndex,
            selectedOptionIds = selectedOptionId?.let { setOf(it) } ?: emptySet(),
            isSubmitted = isSubmitted,
            isCorrect = isCorrect,
            score = score
        )
    }

    data class Completed(
        val levelId: String,
        val levelTitle: String,
        val totalQuestions: Int,
        val correctAnswers: Int,
        val xpEarned: Int,
        val isPassed: Boolean = true
    ) : QuizUiState

    data class Error(val message: String) : QuizUiState
}
