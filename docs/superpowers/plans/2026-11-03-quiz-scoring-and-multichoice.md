# Quiz Scoring 80% Threshold & Multi-Choice Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement an 80% passing threshold requirement to unlock subsequent levels/themes, award completion XP and show a celebration screen with congratulations and a trophy 🏆 reward, while fixing multiple-choice evaluation so that exercises with multiple correct answers require all correct options to be selected.

**Architecture:**
- **Domain:** Update `EvaluateAnswerUseCase` to evaluate a `Set<String>` of selected option IDs against the full set of correct options in an `Exercise`. An exercise is only correct if the selected set matches the correct set exactly.
- **Presentation (MVVM):** Update `QuizUiState` and `QuizViewModel` to hold `selectedOptionIds: Set<String>` with toggle support, calculate completion accuracy percentage (`(correct * 100) / total`), gate `completeLevelUseCase` behind `accuracy >= 80%`, and distinguish passed vs failed completion states.
- **UI (Compose):** In `QuizScreen`, render `OptionIndicatorType.MULTI_CHOICE` when an exercise has multiple correct answers, and present a celebration screen with trophy 🏆, medal 🏅, and raccoon celebration when passed, or a retry requirement screen when `< 80%`.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Clean Architecture (Package-by-Feature), StateFlow, JUnit 4, Kotlinx Coroutines Test.

---

## Global Constraints
- Clean Architecture inside `feature/quiz/`: Domain remains pure Kotlin (no `android.*` imports).
- Follow Design System from `AGENTS.md`: Use `CodeQuizOptionCard`, `CodeQuizCard`, `CodeQuizButton`, and `CodeQuizMascot`.
- Do not modify raw database entities or schema unless strictly necessary; existing catalog exercises remain untouched.
- All test suites must pass (`./gradlew testDebugUnitTest`).

## Review Focus
1. An exercise with multiple correct options is evaluated as false when only 1 correct option is selected.
2. An exercise with multiple correct options is evaluated as false when all correct options plus 1 incorrect option are selected.
3. A completed level with `< 80%` accuracy does not call `completeLevelUseCase` and awards 0 completion XP.
4. A completed level with `>= 80%` accuracy calls `completeLevelUseCase`, records daily activity, and displays the celebration trophy UI.
5. In a single-choice exercise, tapping a different option switches selection; in a multi-choice exercise, tapping toggles the option without clearing others.

---

### Task 1: Refactor `EvaluateAnswerUseCase` for Multi-Choice Answer Validation

**Files:**
- Modify: `app/src/main/java/com/code/Quiz/feature/quiz/domain/usecase/EvaluateAnswerUseCase.kt`
- Test: `app/src/test/java/com/code/Quiz/feature/quiz/domain/usecase/EvaluateAnswerUseCaseTest.kt`

**Interfaces:**
- Produces: `EvaluateAnswerUseCase.invoke(exercise: Exercise, selectedOptionIds: Set<String>): Boolean`
- Backward-compatibility overload (optional): `EvaluateAnswerUseCase.invoke(exercise: Exercise, selectedOptionId: String): Boolean`

- [ ] **Step 1: Write failing unit tests for multi-choice evaluation in `EvaluateAnswerUseCaseTest.kt`**

```kotlin
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.feature.quiz.domain.usecase.EvaluateAnswerUseCaseTest`
Expected: Compilation failure or FAIL (overload with `Set<String>` not implemented yet).

- [ ] **Step 3: Implement `EvaluateAnswerUseCase` with exact set comparison**

```kotlin
package com.code.Quiz.feature.quiz.domain.usecase

import com.code.Quiz.feature.quiz.domain.model.Exercise

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
```

- [ ] **Step 4: Run tests to verify all pass**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.feature.quiz.domain.usecase.EvaluateAnswerUseCaseTest`
Expected: PASS

---

### Task 2: Update `QuizUiState` and `QuizViewModel` for Multi-Choice Selection & 80% Progression Rule

**Files:**
- Modify: `app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizUiState.kt`
- Modify: `app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizViewModel.kt`
- Test: `app/src/test/java/com/code/Quiz/feature/quiz/ui/QuizViewModelTest.kt`

**Interfaces:**
- `QuizUiState.Content`: `val selectedOptionIds: Set<String> = emptySet()`
- `QuizUiState.Completed`: `val isPassed: Boolean`
- `QuizViewModel.toggleOption(optionId: String)`: Handles single vs multi-choice selection
- `QuizViewModel.submitAnswer()`: Evaluates using `selectedOptionIds`
- `QuizViewModel.nextExercise()`: Applies 80% rule: `val accuracy = (correctAnswers * 100) / totalQuestions; val isPassed = accuracy >= 80`

- [ ] **Step 1: Write failing unit tests in `QuizViewModelTest.kt`**

Add tests:
- `toggleOption_inMultiChoiceExercise_addsAndRemovesOptions()`
- `nextExercise_onLastExercise_whenAccuracyLessThan80Percent_doesNotCompleteLevelAndMarksNotPassed()`
- `nextExercise_onLastExercise_whenAccuracyAtLeast80Percent_completesLevelAndMarksPassed()`

- [ ] **Step 2: Run tests to verify failure**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.feature.quiz.ui.QuizViewModelTest`
Expected: FAIL

- [ ] **Step 3: Update `QuizUiState.kt` and `QuizViewModel.kt`**

In `QuizUiState.kt`:
- Replace `selectedOptionId: String?` in `Content` with `selectedOptionIds: Set<String> = emptySet()`.
- Add `val isPassed: Boolean` to `Completed`.

In `QuizViewModel.kt`:
- Add `fun toggleOption(optionId: String)`:
  - Check if current exercise has `options.count { it.isCorrect } > 1`.
  - If multi-choice: toggle `optionId` in `selectedOptionIds`.
  - If single-choice: `selectedOptionIds = setOf(optionId)`.
- Keep `selectOption(optionId: String) = toggleOption(optionId)` for backwards compatibility.
- In `submitAnswer()`:
  - Pass `state.selectedOptionIds` to `evaluateAnswerUseCase(exercise, state.selectedOptionIds)`.
- In `nextExercise()`:
  - Calculate `val accuracy = (correctAnswers * 100) / totalQuestions.coerceAtLeast(1)`.
  - `val isPassed = accuracy >= 80`.
  - If `isPassed`: call `completeLevelUseCase(...)`, `recordDailyActivityUseCase(xpEarned, correctAnswers)`, `isPassed = true`.
  - If not `isPassed`: DO NOT call `completeLevelUseCase`, set `xpEarned = 0`, `isPassed = false`.

- [ ] **Step 4: Run tests to verify all pass**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.feature.quiz.ui.QuizViewModelTest`
Expected: PASS

---

### Task 3: Update `QuizScreen.kt` for Multi-Choice Indicators and Celebration / Trophy Rewards Screen

**Files:**
- Modify: `app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizScreen.kt`

**Details:**
- [ ] **Step 1: Update Exercise Options rendering in `QuizScreen.kt`**
  - Detect if exercise is multi-choice: `val isMultiChoice = exercise.options.count { it.isCorrect } > 1`.
  - Indicator type: `val indicatorType = if (isMultiChoice) OptionIndicatorType.MULTI_CHOICE else OptionIndicatorType.SINGLE_CHOICE`.
  - Card state:
    - If `!uiState.isSubmitted`: `if (option.id in uiState.selectedOptionIds) SELECTED else DEFAULT`.
    - If `uiState.isSubmitted`: `if (option.isCorrect) CORRECT else if (option.id in uiState.selectedOptionIds) INCORRECT else DEFAULT`.
  - "Comprobar" button enabled: `enabled = uiState.selectedOptionIds.isNotEmpty()`.
  - Subtitle hint when multi-choice: Show a small tag or text `"Selecciona todas las opciones correctas"` when `isMultiChoice`.

- [ ] **Step 2: Update `QuizUiState.Completed` screen presentation**
  - **When `uiState.isPassed == true`**:
    - Title: `"¡Felicidades! 🎉"`
    - Subtitle: `uiState.levelTitle` + `"¡Nivel Superado con Éxito!"`
    - Mascot: `CodeQuizMascot(state = MascotState.CELEBRATION)`
    - Rewards Card with Trophy 🏆 and Medal 🏅 badge:
      - `+${uiState.xpEarned} XP`
      - `Aciertos: ${uiState.correctAnswers}/${uiState.totalQuestions} (${accuracy}%)`
      - `"🏆 ¡Premio del Nivel Desbloqueado!"`
    - Action button: `"Continuar al Mapa de Niveles" -> onExit()` (next level is now unlocked).
  - **When `uiState.isPassed == false`**:
    - Title: `"Nivel No Superado"`
    - Subtitle: `"Necesitas al menos 80% de aciertos para desbloquear el siguiente tema."`
    - Mascot: `CodeQuizMascot(state = MascotState.ERROR)`
    - Stats Card:
      - `0 XP`
      - `Aciertos: ${uiState.correctAnswers}/${uiState.totalQuestions} (${accuracy}%)`
      - Notice: `"Mínimo requerido: 80%"`
    - Action buttons:
      - Primary: `"Reintentar Nivel" -> onRetry()`
      - Secondary: `"Volver al Mapa" -> onExit()`

---

### Task 4: Compilation, Unit Tests & Emulator Verification

**Files:**
- Verify full suite across the application.

- [ ] **Step 1: Run all unit tests**
  Run: `./gradlew testDebugUnitTest`
  Expected: BUILD SUCCESSFUL (all tests passing).

- [ ] **Step 2: Compile & Install APK on running emulator**
  Run: `./gradlew installDebug`
  Expected: BUILD SUCCESSFUL and installed on `emulator-5554`.

- [ ] **Step 3: Launch app on emulator and verify runtime execution**
  Run: `adb shell am start -n com.code.Quiz/.MainActivity`
  Expected: App launches cleanly with no crashes.
