package com.code.Quiz.feature.quiz.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.ErrorRed
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.DifficultyBadge
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.badge.DifficultyBadge
import com.code.Quiz.core.ui.component.button.CodeQuizButtonVariant
import com.code.Quiz.core.ui.component.button.CodeQuizCloseButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.card.CodeQuizOptionCard
import com.code.Quiz.core.ui.component.card.OptionIndicatorType
import com.code.Quiz.core.ui.component.card.OptionSelectionState
import com.code.Quiz.core.ui.component.code.CodeSnippetBlock
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import com.code.Quiz.core.ui.component.progress.CodeQuizProgressBar
import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful composable for the interactive Quiz / Level Micro-Challenge Screen.
 */
@Composable
fun QuizScreen(
    levelId: String,
    levelTitle: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = koinViewModel()
) {
    LaunchedEffect(levelId, levelTitle) {
        viewModel.loadLevel(levelId, levelTitle)
    }

    val uiState by viewModel.uiState.collectAsState()

    QuizScreenContent(
        uiState = uiState,
        onOptionSelected = viewModel::selectOption,
        onAnswerSubmit = viewModel::submitAnswer,
        onNextExercise = viewModel::nextExercise,
        onRetry = viewModel::retry,
        onExit = onExit,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable rendering the quiz states.
 */
@Composable
fun QuizScreenContent(
    uiState: QuizUiState,
    onOptionSelected: (String) -> Unit,
    onAnswerSubmit: () -> Unit,
    onNextExercise: () -> Unit,
    onRetry: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        Dialog(onDismissRequest = { showExitDialog = false }) {
            CodeQuizCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md)
            ) {
                Text(
                    text = "¿Abandonar el nivel?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Perderás el progreso de esta sesión de retos. ¿Estás seguro de que deseas salir?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    CodeQuizButton(
                        text = "Continuar nivel",
                        onClick = { showExitDialog = false },
                        modifier = Modifier.weight(1f)
                    )
                    CodeQuizButton(
                        text = "Salir",
                        onClick = {
                            showExitDialog = false
                            onExit()
                        },
                        variant = CodeQuizButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    when (uiState) {
        is QuizUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CodeQuizMascot(
                        state = MascotState.THINKING,
                        modifier = Modifier.size(160.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Preparando tus micro-retos...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }

        is QuizUiState.Content -> {
            val exercise = uiState.exercises.getOrNull(uiState.currentIndex)
            if (exercise == null) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CodeQuizButton(text = "Reintentar", onClick = onRetry)
                }
                return
            }

            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = DarkBackground,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Close Button (✕)
                        CodeQuizCloseButton(onClick = { showExitDialog = true })

                        // Progress Bar showing (currentIndex + 1) / total
                        val progress = (uiState.currentIndex + 1).toFloat() / uiState.exercises.size.toFloat()
                        CodeQuizProgressBar(
                            progress = progress,
                            modifier = Modifier.weight(1f)
                        )

                        // Level Counter
                        Text(
                            text = "${uiState.currentIndex + 1}/${uiState.exercises.size}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        )
                    }
                },
                bottomBar = {
                    AnimatedContent(
                        targetState = uiState.isSubmitted,
                        label = "BottomFeedbackTransition"
                    ) { isSubmitted ->
                        if (!isSubmitted) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DarkSurfaceBorder)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                                        .navigationBarsPadding()
                                ) {
                                    CodeQuizButton(
                                        text = "Comprobar",
                                        onClick = onAnswerSubmit,
                                        enabled = uiState.selectedOptionIds.isNotEmpty(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            val backgroundColor = if (uiState.isCorrect) {
                                SuccessMint.copy(alpha = 0.15f)
                            } else {
                                ErrorRed.copy(alpha = 0.15f)
                            }
                            val borderColor = if (uiState.isCorrect) {
                                SuccessMint.copy(alpha = 0.4f)
                            } else {
                                ErrorRed.copy(alpha = 0.4f)
                            }
                            val titleColor = if (uiState.isCorrect) SuccessMint else ErrorRed
                            val mascotState = if (uiState.isCorrect) MascotState.SUCCESS else MascotState.ERROR
                            val feedbackTitle = if (uiState.isCorrect) "¡Excelente! 🎉" else "¡Casi! Revisa el concepto"

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = DarkSurface,
                                border = BorderStroke(1.5.dp, borderColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(backgroundColor)
                                        .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                                        .navigationBarsPadding()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                                    ) {
                                        CodeQuizMascot(
                                            state = mascotState,
                                            modifier = Modifier.size(68.dp)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(max = 140.dp)
                                                .verticalScroll(rememberScrollState())
                                        ) {
                                            Text(
                                                text = feedbackTitle,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = titleColor
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(Spacing.xs))
                                            Text(
                                                text = exercise.explanation,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextPrimary,
                                                    lineHeight = 18.sp
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.md))

                                    CodeQuizButton(
                                        text = "Continuar ➔",
                                        onClick = onNextExercise,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Difficulty indicator & language chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        DifficultyBadge(difficulty = exercise.difficulty)

                        if (exercise.language.isNotBlank()) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = exercise.language,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = TextSecondary
                            )
                        }
                    }

                    // Question text
                    Text(
                        text = exercise.question,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 28.sp
                        ),
                        color = TextPrimary
                    )

                    // Monospace Code Block when present
                    if (!exercise.codeSnippet.isNullOrBlank()) {
                        CodeSnippetBlock(
                            code = exercise.codeSnippet,
                            language = exercise.language
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    // Multi-choice instruction hint
                    val isMultiChoice = exercise.options.count { it.isCorrect } > 1
                    val indicatorType = if (isMultiChoice) {
                        OptionIndicatorType.MULTI_CHOICE
                    } else {
                        OptionIndicatorType.SINGLE_CHOICE
                    }

                    if (isMultiChoice) {
                        Text(
                            text = "Selecciona todas las opciones correctas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = AccentCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Answer Options Cards
                    exercise.options.forEachIndexed { index, option ->
                        val isSelected = option.id in uiState.selectedOptionIds
                        val cardState = when {
                            !uiState.isSubmitted -> {
                                if (isSelected) {
                                    OptionSelectionState.SELECTED
                                } else {
                                    OptionSelectionState.DEFAULT
                                }
                            }
                            option.isCorrect -> OptionSelectionState.CORRECT
                            isSelected -> OptionSelectionState.INCORRECT
                            else -> OptionSelectionState.DEFAULT
                        }

                        CodeQuizOptionCard(
                            text = option.text,
                            label = ('A' + index).toString(),
                            state = cardState,
                            indicatorType = indicatorType,
                            enabled = !uiState.isSubmitted,
                            onClick = { onOptionSelected(option.id) }
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }

        is QuizUiState.Completed -> {
            val accuracy = (uiState.correctAnswers * 100) / uiState.totalQuestions.coerceAtLeast(1)

            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = DarkBackground
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = Spacing.lg)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isPassed) {
                            CodeQuizMascot(
                                state = MascotState.CELEBRATION,
                                modifier = Modifier.size(190.dp)
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Text(
                                text = "¡Felicidades! 🎉",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(Spacing.xs))

                            Text(
                                text = "${uiState.levelTitle} • ¡Nivel Superado con Éxito!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(Spacing.lg))

                            // Rewards card with Trophy & Medal
                            CodeQuizCard(
                                modifier = Modifier.fillMaxWidth(),
                                variant = CodeQuizCardVariant.Elevated
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Spacing.sm),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // XP Earned
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "+${uiState.xpEarned} XP",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessMint
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(Spacing.xs))
                                            Text(
                                                text = "Experiencia",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }

                                        // Accuracy
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$accuracy%",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentCyan
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(Spacing.xs))
                                            Text(
                                                text = "Aciertos (${uiState.correctAnswers}/${uiState.totalQuestions})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.md))

                                    // Prize Trophy Banner
                                    Surface(
                                        color = SuccessMint.copy(alpha = 0.12f),
                                        shape = MaterialTheme.shapes.small,
                                        border = BorderStroke(1.dp, SuccessMint.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "🏆 Premio de Nivel Desbloqueado 🏅",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessMint
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            CodeQuizMascot(
                                state = MascotState.ERROR,
                                modifier = Modifier.size(160.dp)
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            Text(
                                text = "Nivel No Superado",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(Spacing.xs))

                            Text(
                                text = "Necesitas al menos 80% de aciertos para desbloquear el siguiente tema.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(Spacing.lg))

                            // Stats card showing required vs obtained
                            CodeQuizCard(
                                modifier = Modifier.fillMaxWidth(),
                                variant = CodeQuizCardVariant.Elevated
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Spacing.sm),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 0 XP Earned
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "0 XP",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextSecondary
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(Spacing.xs))
                                            Text(
                                                text = "Experiencia",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }

                                        // Accuracy
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$accuracy%",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ErrorRed
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(Spacing.xs))
                                            Text(
                                                text = "Aciertos (${uiState.correctAnswers}/${uiState.totalQuestions})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.md))

                                    // Requirement warning
                                    Surface(
                                        color = ErrorRed.copy(alpha = 0.12f),
                                        shape = MaterialTheme.shapes.small,
                                        border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Mínimo requerido para avanzar: 80%",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ErrorRed
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    // Buttons
                    if (uiState.isPassed) {
                        CodeQuizButton(
                            text = "Continuar al Mapa de Niveles",
                            onClick = onExit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Spacing.xl)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Spacing.xl),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            CodeQuizButton(
                                text = "Reintentar Nivel",
                                onClick = onRetry,
                                modifier = Modifier.fillMaxWidth()
                            )
                            CodeQuizButton(
                                text = "Volver al Mapa",
                                onClick = onExit,
                                variant = CodeQuizButtonVariant.Secondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        is QuizUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(Spacing.lg)
                ) {
                    CodeQuizMascot(
                        state = MascotState.ERROR,
                        modifier = Modifier.size(140.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    CodeQuizButton(
                        text = "Reintentar",
                        onClick = onRetry
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewExercises = listOf(
    Exercise(
        id = "ex_1",
        levelId = "intro_prog_level_1",
        question = "¿Cuál es la palabra clave en Kotlin para declarar una variable inmutable de solo lectura?",
        codeSnippet = "val total = 100\n// total = 200 // Error",
        language = "Kotlin",
        options = listOf(
            ExerciseOption("opt_1", "var", false),
            ExerciseOption("opt_2", "val", true),
            ExerciseOption("opt_3", "let", false),
            ExerciseOption("opt_4", "const", false)
        ),
        explanation = "En Kotlin, 'val' declara referencias inmutables (de solo lectura)."
    )
)

@Preview(name = "Quiz Active Question", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizScreenContentActivePreview() {
    CodeQuizTheme {
        QuizScreenContent(
            uiState = QuizUiState.Content(
                levelId = "intro_prog_level_1",
                levelTitle = "Variables y Tipos de Datos",
                exercises = previewExercises,
                currentIndex = 0,
                selectedOptionId = "opt_2",
                isSubmitted = false,
                isCorrect = false,
                score = 0
            ),
            onOptionSelected = {},
            onAnswerSubmit = {},
            onNextExercise = {},
            onRetry = {},
            onExit = {}
        )
    }
}

@Preview(name = "Quiz Submitted Correct", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizScreenContentCorrectPreview() {
    CodeQuizTheme {
        QuizScreenContent(
            uiState = QuizUiState.Content(
                levelId = "intro_prog_level_1",
                levelTitle = "Variables y Tipos de Datos",
                exercises = previewExercises,
                currentIndex = 0,
                selectedOptionId = "opt_2",
                isSubmitted = true,
                isCorrect = true,
                score = 1
            ),
            onOptionSelected = {},
            onAnswerSubmit = {},
            onNextExercise = {},
            onRetry = {},
            onExit = {}
        )
    }
}

@Preview(name = "Quiz Submitted Incorrect", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizScreenContentIncorrectPreview() {
    CodeQuizTheme {
        QuizScreenContent(
            uiState = QuizUiState.Content(
                levelId = "intro_prog_level_1",
                levelTitle = "Variables y Tipos de Datos",
                exercises = previewExercises,
                currentIndex = 0,
                selectedOptionId = "opt_1",
                isSubmitted = true,
                isCorrect = false,
                score = 0
            ),
            onOptionSelected = {},
            onAnswerSubmit = {},
            onNextExercise = {},
            onRetry = {},
            onExit = {}
        )
    }
}

@Preview(name = "Quiz Completed Celebration (Passed)", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizScreenContentCompletedPreview() {
    CodeQuizTheme {
        QuizScreenContent(
            uiState = QuizUiState.Completed(
                levelId = "intro_prog_level_1",
                levelTitle = "Variables y Tipos de Datos",
                totalQuestions = 5,
                correctAnswers = 4,
                xpEarned = 40,
                isPassed = true
            ),
            onOptionSelected = {},
            onAnswerSubmit = {},
            onNextExercise = {},
            onRetry = {},
            onExit = {}
        )
    }
}

@Preview(name = "Quiz Completed (Failed <80%)", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizScreenContentFailedPreview() {
    CodeQuizTheme {
        QuizScreenContent(
            uiState = QuizUiState.Completed(
                levelId = "intro_prog_level_1",
                levelTitle = "Variables y Tipos de Datos",
                totalQuestions = 5,
                correctAnswers = 2,
                xpEarned = 0,
                isPassed = false
            ),
            onOptionSelected = {},
            onAnswerSubmit = {},
            onNextExercise = {},
            onRetry = {},
            onExit = {}
        )
    }
}
