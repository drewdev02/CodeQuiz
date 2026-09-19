package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve exercises for a specific level.
 */
class GetLevelExercisesUseCase(
    private val quizRepository: QuizRepository
) {
    operator fun invoke(levelId: String): Flow<List<Exercise>> =
        quizRepository.getExercisesForLevel(levelId)
}
