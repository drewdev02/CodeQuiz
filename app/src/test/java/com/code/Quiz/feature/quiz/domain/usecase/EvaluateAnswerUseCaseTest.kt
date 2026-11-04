package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EvaluateAnswerUseCaseTest {

    private lateinit var evaluateAnswerUseCase: EvaluateAnswerUseCase

    private val testExercise = Exercise(
        id = "ex_1",
        levelId = "lvl_1",
        question = "¿Cuál es inmutable?",
        codeSnippet = "val x = 1",
        options = listOf(
            ExerciseOption(id = "opt_1", text = "var", isCorrect = false),
            ExerciseOption(id = "opt_2", text = "val", isCorrect = true)
        ),
        explanation = "val es inmutable"
    )

    @Before
    fun setUp() {
        evaluateAnswerUseCase = EvaluateAnswerUseCase()
    }

    @Test
    fun invoke_whenSelectedOptionIsCorrect_returnsTrue() {
        val result = evaluateAnswerUseCase(testExercise, "opt_2")
        assertTrue(result)
    }

    @Test
    fun invoke_whenSelectedOptionIsIncorrect_returnsFalse() {
        val result = evaluateAnswerUseCase(testExercise, "opt_1")
        assertFalse(result)
    }

    @Test
    fun invoke_whenSelectedOptionDoesNotExist_returnsFalse() {
        val result = evaluateAnswerUseCase(testExercise, "opt_999")
        assertFalse(result)
    }

    @Test
    fun invoke_whenMultipleCorrectOptionsAndOnlyOneSelected_returnsFalse() {
        val multiExercise = testExercise.copy(
            options = listOf(
                ExerciseOption("opt_1", "A", true),
                ExerciseOption("opt_2", "B", true),
                ExerciseOption("opt_3", "C", false)
            )
        )
        val result = evaluateAnswerUseCase(multiExercise, setOf("opt_1"))
        assertFalse(result)
    }

    @Test
    fun invoke_whenMultipleCorrectOptionsAndAllSelected_returnsTrue() {
        val multiExercise = testExercise.copy(
            options = listOf(
                ExerciseOption("opt_1", "A", true),
                ExerciseOption("opt_2", "B", true),
                ExerciseOption("opt_3", "C", false)
            )
        )
        val result = evaluateAnswerUseCase(multiExercise, setOf("opt_1", "opt_2"))
        assertTrue(result)
    }

    @Test
    fun invoke_whenCorrectOptionsSelectedWithExtraIncorrect_returnsFalse() {
        val multiExercise = testExercise.copy(
            options = listOf(
                ExerciseOption("opt_1", "A", true),
                ExerciseOption("opt_2", "B", true),
                ExerciseOption("opt_3", "C", false)
            )
        )
        val result = evaluateAnswerUseCase(multiExercise, setOf("opt_1", "opt_2", "opt_3"))
        assertFalse(result)
    }
}
