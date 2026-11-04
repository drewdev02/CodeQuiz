package com.code.Quiz.feature.quiz.ui

import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import com.code.Quiz.feature.quiz.domain.repository.QuizRepository
import com.code.Quiz.feature.quiz.domain.usecase.CompleteLevelUseCase
import com.code.Quiz.feature.quiz.domain.usecase.EvaluateAnswerUseCase
import com.code.Quiz.feature.quiz.domain.usecase.GetLevelExercisesUseCase
import com.code.Quiz.feature.streak.domain.model.DailyActivity
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import com.code.Quiz.feature.streak.domain.repository.StreakRepository
import com.code.Quiz.feature.streak.domain.usecase.RecordDailyActivityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQuizRepository
    private lateinit var fakeStreakRepository: FakeStreakRepository
    private lateinit var viewModel: QuizViewModel

    private val sampleExercises = listOf(
        Exercise(
            id = "ex_1",
            levelId = "intro_prog_level_1",
            question = "¿Cuál es inmutable?",
            codeSnippet = "val x = 1",
            options = listOf(
                ExerciseOption("opt_1", "var", false),
                ExerciseOption("opt_2", "val", true)
            ),
            explanation = "val es inmutable"
        ),
        Exercise(
            id = "ex_2",
            levelId = "intro_prog_level_1",
            question = "¿Qué imprime println(2 + 2)?",
            codeSnippet = "println(2 + 2)",
            options = listOf(
                ExerciseOption("opt_3", "4", true),
                ExerciseOption("opt_4", "22", false)
            ),
            explanation = "Suma aritmética 2 + 2 = 4"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQuizRepository(sampleExercises)
        fakeStreakRepository = FakeStreakRepository()
        viewModel = QuizViewModel(
            getLevelExercisesUseCase = GetLevelExercisesUseCase(fakeRepository),
            completeLevelUseCase = CompleteLevelUseCase(fakeRepository),
            recordDailyActivityUseCase = RecordDailyActivityUseCase(fakeStreakRepository),
            evaluateAnswerUseCase = EvaluateAnswerUseCase()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadLevel_loadsExercisesAndSetsInitialContentState() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Content", state is QuizUiState.Content)
        val content = state as QuizUiState.Content
        assertEquals("intro_prog_level_1", content.levelId)
        assertEquals("Variables y Tipos", content.levelTitle)
        assertEquals(2, content.exercises.size)
        assertEquals(0, content.currentIndex)
        assertEquals(emptySet<String>(), content.selectedOptionIds)
        assertFalse(content.isSubmitted)
        assertEquals(0, content.score)
    }

    @Test
    fun selectOption_updatesSelectedOptionIds() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        viewModel.selectOption("opt_2")
        val content = viewModel.uiState.value as QuizUiState.Content
        assertEquals(setOf("opt_2"), content.selectedOptionIds)
    }

    @Test
    fun toggleOption_inMultiChoiceExercise_addsAndRemovesOptions() = runTest {
        val multiChoiceExercises = listOf(
            Exercise(
                id = "multi_ex",
                levelId = "lvl_multi",
                question = "Selecciona todas las correctas",
                options = listOf(
                    ExerciseOption("opt_1", "A", true),
                    ExerciseOption("opt_2", "B", true),
                    ExerciseOption("opt_3", "C", false)
                ),
                explanation = "A y B son correctas"
            )
        )
        val multiRepo = FakeQuizRepository(multiChoiceExercises)
        val multiVm = QuizViewModel(
            getLevelExercisesUseCase = GetLevelExercisesUseCase(multiRepo),
            completeLevelUseCase = CompleteLevelUseCase(multiRepo),
            recordDailyActivityUseCase = RecordDailyActivityUseCase(fakeStreakRepository),
            evaluateAnswerUseCase = EvaluateAnswerUseCase()
        )
        multiVm.loadLevel("lvl_multi", "Multi Level")
        advanceUntilIdle()

        multiVm.toggleOption("opt_1")
        assertEquals(setOf("opt_1"), (multiVm.uiState.value as QuizUiState.Content).selectedOptionIds)

        multiVm.toggleOption("opt_2")
        assertEquals(setOf("opt_1", "opt_2"), (multiVm.uiState.value as QuizUiState.Content).selectedOptionIds)

        multiVm.toggleOption("opt_1")
        assertEquals(setOf("opt_2"), (multiVm.uiState.value as QuizUiState.Content).selectedOptionIds)
    }

    @Test
    fun submitAnswer_whenCorrect_setsSubmittedTrueAndIncrementsScore() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        viewModel.selectOption("opt_2") // opt_2 is correct
        viewModel.submitAnswer()

        val content = viewModel.uiState.value as QuizUiState.Content
        assertTrue(content.isSubmitted)
        assertTrue(content.isCorrect)
        assertEquals(1, content.score)
    }

    @Test
    fun submitAnswer_whenIncorrect_setsSubmittedTrueAndDoesNotIncrementScore() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        viewModel.selectOption("opt_1") // opt_1 is incorrect
        viewModel.submitAnswer()

        val content = viewModel.uiState.value as QuizUiState.Content
        assertTrue(content.isSubmitted)
        assertFalse(content.isCorrect)
        assertEquals(0, content.score)
    }

    @Test
    fun nextExercise_advancesToNextIndexAndResetsSelection() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        viewModel.selectOption("opt_2")
        viewModel.submitAnswer()
        viewModel.nextExercise()

        val content = viewModel.uiState.value as QuizUiState.Content
        assertEquals(1, content.currentIndex)
        assertEquals(emptySet<String>(), content.selectedOptionIds)
        assertFalse(content.isSubmitted)
        assertFalse(content.isCorrect)
        assertEquals(1, content.score)
    }

    @Test
    fun nextExercise_onLastExercise_whenAccuracyLessThan80Percent_doesNotCompleteLevelAndMarksNotPassed() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        // Total: 2 questions. 1 correct = 50% (< 80%)
        // Question 1: correct
        viewModel.selectOption("opt_2")
        viewModel.submitAnswer()
        viewModel.nextExercise()

        // Question 2 (last): incorrect
        viewModel.selectOption("opt_4")
        viewModel.submitAnswer()
        viewModel.nextExercise()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Completed", state is QuizUiState.Completed)
        val completed = state as QuizUiState.Completed
        assertEquals("intro_prog_level_1", completed.levelId)
        assertEquals(2, completed.totalQuestions)
        assertEquals(1, completed.correctAnswers)
        assertFalse("Level should NOT be marked as passed", completed.isPassed)
        assertEquals(0, completed.xpEarned)
        assertEquals("completeLevel should NOT be called when < 80%", 0, fakeRepository.completeLevelCallCount)
    }

    @Test
    fun nextExercise_onLastExercise_whenAccuracyAtLeast80Percent_completesLevelAndMarksPassed() = runTest {
        viewModel.loadLevel("intro_prog_level_1", "Variables y Tipos")
        advanceUntilIdle()

        // Question 1: correct
        viewModel.selectOption("opt_2")
        viewModel.submitAnswer()
        viewModel.nextExercise()

        // Question 2 (last): correct (2/2 = 100% >= 80%)
        viewModel.selectOption("opt_3")
        viewModel.submitAnswer()
        viewModel.nextExercise()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Completed", state is QuizUiState.Completed)
        val completed = state as QuizUiState.Completed
        assertEquals("intro_prog_level_1", completed.levelId)
        assertEquals(2, completed.totalQuestions)
        assertEquals(2, completed.correctAnswers)
        assertTrue("Level should be marked as passed", completed.isPassed)
        assertTrue("XP earned should be > 0", completed.xpEarned > 0)
        assertEquals(1, fakeRepository.completeLevelCallCount)
        assertEquals(1, fakeStreakRepository.recordActivityCallCount)
        assertEquals(completed.xpEarned, fakeStreakRepository.lastXpEarned)
        assertEquals(2, fakeStreakRepository.lastExercisesCount)
    }

    class FakeStreakRepository : StreakRepository {
        var recordActivityCallCount = 0
        var lastXpEarned = 0
        var lastExercisesCount = 0

        override fun getStreak(): Flow<Streak> = emptyFlow()
        override fun getRecentActivities(limit: Int): Flow<List<DailyActivity>> = emptyFlow()
        override fun getWeekStreak(): Flow<List<WeekDayStatus>> = emptyFlow()

        override suspend fun recordActivity(xpEarned: Int, exercisesCount: Int) {
            recordActivityCallCount++
            lastXpEarned = xpEarned
            lastExercisesCount = exercisesCount
        }
    }

    class FakeQuizRepository(
        private val exercises: List<Exercise>
    ) : QuizRepository {
        var completeLevelCallCount = 0

        override fun getExercisesForLevel(levelId: String): Flow<List<Exercise>> =
            flowOf(exercises)

        override suspend fun completeLevel(courseId: String, levelId: String, xpEarned: Int) {
            completeLevelCallCount++
        }
    }
}
