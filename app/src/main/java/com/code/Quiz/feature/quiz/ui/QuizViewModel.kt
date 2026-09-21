package com.code.Quiz.feature.quiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.quiz.domain.usecase.CompleteLevelUseCase
import com.code.Quiz.feature.quiz.domain.usecase.EvaluateAnswerUseCase
import com.code.Quiz.feature.quiz.domain.usecase.GetLevelExercisesUseCase
import com.code.Quiz.feature.streak.domain.usecase.RecordDailyActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel orchestrating the interactive quiz/level micro-challenges,
 * answer evaluation, progression, and completion.
 */
class QuizViewModel(
    private val getLevelExercisesUseCase: GetLevelExercisesUseCase,
    private val completeLevelUseCase: CompleteLevelUseCase,
    private val recordDailyActivityUseCase: RecordDailyActivityUseCase,
    private val evaluateAnswerUseCase: EvaluateAnswerUseCase = EvaluateAnswerUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentLevelId: String = ""
    private var currentLevelTitle: String = ""

    fun loadLevel(levelId: String, levelTitle: String) {
        currentLevelId = levelId
        currentLevelTitle = levelTitle
        _uiState.value = QuizUiState.Loading

        viewModelScope.launch {
            getLevelExercisesUseCase(levelId)
                .catch { error ->
                    _uiState.value = QuizUiState.Error(
                        message = error.localizedMessage ?: "Error al cargar los ejercicios del nivel."
                    )
                }
                .collect { exercises ->
                    if (exercises.isEmpty()) {
                        _uiState.value = QuizUiState.Error(
                            message = "No se encontraron ejercicios disponibles para este nivel."
                        )
                    } else {
                        _uiState.value = QuizUiState.Content(
                            levelId = levelId,
                            levelTitle = levelTitle,
                            exercises = exercises,
                            currentIndex = 0,
                            selectedOptionId = null,
                            isSubmitted = false,
                            isCorrect = false,
                            score = 0
                        )
                    }
                }
        }
    }

    fun selectOption(optionId: String) {
        val state = _uiState.value
        if (state is QuizUiState.Content && !state.isSubmitted) {
            _uiState.value = state.copy(selectedOptionId = optionId)
        }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state is QuizUiState.Content && state.selectedOptionId != null && !state.isSubmitted) {
            val exercise = state.exercises.getOrNull(state.currentIndex) ?: return
            val isCorrect = evaluateAnswerUseCase(exercise, state.selectedOptionId)
            val updatedScore = if (isCorrect) state.score + 1 else state.score

            _uiState.value = state.copy(
                isSubmitted = true,
                isCorrect = isCorrect,
                score = updatedScore
            )
        }
    }

    fun nextExercise() {
        val state = _uiState.value
        if (state is QuizUiState.Content) {
            if (state.currentIndex + 1 < state.exercises.size) {
                _uiState.value = state.copy(
                    currentIndex = state.currentIndex + 1,
                    selectedOptionId = null,
                    isSubmitted = false,
                    isCorrect = false
                )
            } else {
                // Completed all exercises
                val totalQuestions = state.exercises.size
                val correctAnswers = state.score
                val xpEarned = (correctAnswers * 10).coerceAtLeast(15)

                viewModelScope.launch {
                    completeLevelUseCase(
                        courseId = "",
                        levelId = state.levelId,
                        xpEarned = xpEarned
                    )
                    recordDailyActivityUseCase(
                        xpEarned = xpEarned,
                        exercisesCount = correctAnswers
                    )
                }

                _uiState.value = QuizUiState.Completed(
                    levelId = state.levelId,
                    levelTitle = state.levelTitle,
                    totalQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    xpEarned = xpEarned
                )
            }
        }
    }

    fun retry() {
        if (currentLevelId.isNotBlank()) {
            loadLevel(currentLevelId, currentLevelTitle)
        }
    }
}
