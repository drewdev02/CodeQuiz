package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.repository.QuizRepository

/**
 * Use case to mark a level as completed, award XP, and unlock the next level.
 */
class CompleteLevelUseCase(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(courseId: String, levelId: String, xpEarned: Int) {
        quizRepository.completeLevel(courseId, levelId, xpEarned)
    }
}
