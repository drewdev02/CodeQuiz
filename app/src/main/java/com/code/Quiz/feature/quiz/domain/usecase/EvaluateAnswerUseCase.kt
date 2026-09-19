package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.model.Exercise

/**
 * Use case to evaluate whether a selected option in an exercise is correct.
 */
class EvaluateAnswerUseCase {
    operator fun invoke(exercise: Exercise, selectedOptionId: String): Boolean {
        val selectedOption = exercise.options.firstOrNull { it.id == selectedOptionId }
        return selectedOption?.isCorrect == true
    }
}
