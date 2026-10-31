package com.code.Quiz.feature.course.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.StreakBadge
import com.code.Quiz.core.ui.component.button.CodeQuizBackButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.progress.CodeQuizProgressBar
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus
import com.code.Quiz.feature.course.ui.components.LevelDetailBottomSheet
import com.code.Quiz.feature.course.ui.components.LevelNodeItem
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.usecase.GetStreakUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Stateful entry point for the Gamified Course Levels Map screen.
 * Displays the level progression roadmap for the selected course,
 * overall progress stats, and handles level selection for detailed modal inspection.
 */
@Composable
fun CourseLevelsScreen(
    courseId: String,
    courseTitle: String,
    onBack: () -> Unit,
    onStartLevel: (levelId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseLevelsViewModel = koinViewModel(),
    getStreakUseCase: GetStreakUseCase = koinInject()
) {
    LaunchedEffect(courseId, courseTitle) {
        viewModel.loadLevels(courseId, courseTitle)
    }

    val uiState by viewModel.uiState.collectAsState()
    val streak by getStreakUseCase().collectAsState(initial = Streak(0, 0, null, false))

    CourseLevelsContent(
        uiState = uiState,
        courseTitleFallback = courseTitle,
        streakCount = streak.currentStreak,
        onBack = onBack,
        onSelectLevel = viewModel::onSelectLevel,
        onStartLevel = onStartLevel,
        modifier = modifier
    )
}

/**
 * Stateless content presentation for the Course Levels Map screen.
 */
@Composable
fun CourseLevelsContent(
    uiState: CourseLevelsUiState,
    courseTitleFallback: String,
    onBack: () -> Unit,
    onSelectLevel: (CourseLevel?) -> Unit,
    onStartLevel: (levelId: String, title: String) -> Unit,
    streakCount: Int = 1,
    modifier: Modifier = Modifier
) {
    val displayTitle = if (uiState is CourseLevelsUiState.Success) {
        uiState.courseTitle
    } else {
        courseTitleFallback
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                CodeQuizBackButton(onClick = onBack)

                // Course Title
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Spacing.md)
                )

                // Streak Badge
                StreakBadge(streakCount = streakCount)
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is CourseLevelsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = AccentPurple,
                        strokeWidth = 3.dp
                    )
                }
            }

            is CourseLevelsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CourseLevelsUiState.Success -> {
                val completedCount = uiState.levels.count { it.status == LevelStatus.COMPLETED }
                val totalCount = uiState.levels.size
                val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                val progressPercent = (progress * 100).toInt()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Progress Overview Card
                    CodeQuizCard(
                        variant = CodeQuizCardVariant.Elevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg, vertical = Spacing.xs)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso del curso",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "$completedCount de $totalCount niveles completados ($progressPercent%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        CodeQuizProgressBar(progress = progress)
                    }

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    // Gamified Level Path
                    val currentChallengeLevelId = uiState.levels.firstOrNull { it.status == LevelStatus.ACTIVE }?.id
                        ?: uiState.levels.firstOrNull { it.status != LevelStatus.COMPLETED }?.id

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.xs,
                            bottom = Spacing.xxl
                        )
                    ) {
                        itemsIndexed(
                            items = uiState.levels,
                            key = { _, level -> level.id }
                        ) { index, level ->
                            val isCurrent = level.id == currentChallengeLevelId
                            LevelNodeItem(
                                level = level,
                                isCurrentChallenge = isCurrent,
                                showTopLine = index > 0,
                                showBottomLine = index < uiState.levels.size - 1,
                                onClick = { onSelectLevel(level) }
                            )
                        }
                    }
                }

                // Bottom sheet for level details
                if (uiState.selectedLevel != null) {
                    LevelDetailBottomSheet(
                        level = uiState.selectedLevel,
                        onDismiss = { onSelectLevel(null) },
                        onStartLevel = { levelId, title ->
                            onSelectLevel(null)
                            onStartLevel(levelId, title)
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "CourseLevelsScreen Success Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CourseLevelsScreenPreview() {
    CodeQuizTheme {
        CourseLevelsContent(
            uiState = CourseLevelsUiState.Success(
                courseTitle = "Introducción a la Programación",
                levels = listOf(
                    CourseLevel(
                        id = "lvl_1",
                        courseId = "intro",
                        levelNumber = 1,
                        title = "Variables y Tipos de Datos",
                        description = "Aprende qué es una variable y cómo almacenar datos en memoria.",
                        status = LevelStatus.COMPLETED,
                        totalExercises = 5,
                        xpReward = 30
                    ),
                    CourseLevel(
                        id = "lvl_2",
                        courseId = "intro",
                        levelNumber = 2,
                        title = "Operadores y Expresiones",
                        description = "Operaciones aritméticas, lógicas y de comparación para transformar datos.",
                        status = LevelStatus.COMPLETED,
                        totalExercises = 5,
                        xpReward = 35
                    ),
                    CourseLevel(
                        id = "lvl_3",
                        courseId = "intro",
                        levelNumber = 3,
                        title = "Estructuras Condicionales",
                        description = "Toma decisiones en tu código evaluando expresiones booleanas con if, else y when.",
                        status = LevelStatus.ACTIVE,
                        totalExercises = 6,
                        xpReward = 40
                    ),
                    CourseLevel(
                        id = "lvl_4",
                        courseId = "intro",
                        levelNumber = 4,
                        title = "Bucles y Ciclos",
                        description = "Repite instrucciones eficientemente con bucles while y for.",
                        status = LevelStatus.LOCKED,
                        totalExercises = 6,
                        xpReward = 45
                    )
                )
            ),
            courseTitleFallback = "Introducción a la Programación",
            onBack = {},
            onSelectLevel = {},
            onStartLevel = { _, _ -> }
        )
    }
}
