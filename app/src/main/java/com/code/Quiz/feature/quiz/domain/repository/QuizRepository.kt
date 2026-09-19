package com.code.Quiz.feature.quiz.domain.repository

import com.code.Quiz.feature.quiz.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface contract for loading level exercises and completing levels.
 * Pure Kotlin contract in domain layer.
 */
interface QuizRepository {
    fun getExercisesForLevel(levelId: String): Flow<List<Exercise>>
    suspend fun completeLevel(courseId: String, levelId: String, xpEarned: Int)
}
