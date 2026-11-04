package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.model.Exercise

/**
 * Use case to evaluate whether selected option(s) in an exercise are correct.
 * For single or multi-choice exercises, an answer is strictly correct if and only if
 * the selected options match the exact set of options marked as isCorrect.
 */
class EvaluateAnswerUseCase {

    operator fun invoke(exercise: Exercise, selectedOptionIds: Set<String>): Boolean {
        if (selectedOptionIds.isEmpty()) return false
        val correctOptionIds = exercise.options.filter { it.isCorrect }.map { it.id }.toSet()
        return selectedOptionIds == correctOptionIds
    }

    operator fun invoke(exercise: Exercise, selectedOptionId: String): Boolean {
        return invoke(exercise, setOf(selectedOptionId))
    }
}
