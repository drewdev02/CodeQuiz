package com.code.Quiz.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.StreakBadge
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import com.code.Quiz.feature.profile.ui.components.DuolingoStreakCard
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProgressScreenContent(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun ProgressScreenContent(
    uiState: ProgressUiState,
    modifier: Modifier = Modifier
) {
    val currentStreakCount = when (uiState) {
        is ProgressUiState.Success -> uiState.stats.streak.currentStreak
        else -> 0
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            ) {
                Text(
                    text = "Tu Progreso",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

                StreakBadge(
                    streakCount = currentStreakCount,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is ProgressUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Cargando tus estadísticas...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            is ProgressUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CodeQuizMascot(
                        state = MascotState.ERROR,
                        modifier = Modifier.size(160.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is ProgressUiState.Success -> {
                val stats = uiState.stats
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(Spacing.xs))

                    CodeQuizMascot(
                        state = if (stats.streak.isActiveToday) MascotState.CELEBRATION else MascotState.IDLE,
                        modifier = Modifier.size(160.dp)
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text(
                        text = "Estadísticas y Logros",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Text(
                        text = "Monitorea tus rachas diarias, XP acumulada y retos resueltos en tu camino como programador.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Spacing.sm)
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    // Duolingo Streak Card with Week Row & Month Calendar
                    DuolingoStreakCard(stats = stats)

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    // Dynamic Stat Item Cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        StatItemCard(
                            label = "Racha actual",
                            value = "🔥 ${stats.streak.currentStreak} ${if (stats.streak.currentStreak == 1) "día" else "días"}",
                            valueColor = StreakOrange
                        )

                        StatItemCard(
                            label = "Racha récord",
                            value = "🏆 ${stats.streak.maxStreak} ${if (stats.streak.maxStreak == 1) "día" else "días"}",
                            valueColor = StreakOrange
                        )

                        StatItemCard(
                            label = "Experiencia total",
                            value = "✦ +${stats.totalXp} XP",
                            valueColor = SuccessMint
                        )

                        StatItemCard(
                            label = "Retos resueltos",
                            value = "◎ ${stats.totalExercisesCompleted} micro-retos",
                            valueColor = AccentCyan
                        )

                        StatItemCard(
                            label = "Niveles completados",
                            value = "⭐ ${stats.completedLevelsCount} niveles",
                            valueColor = AccentPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }
}

@Composable
private fun StatItemCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    CodeQuizCard(
        variant = CodeQuizCardVariant.Elevated,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = valueColor
            )
        }
    }
}

@Preview(name = "Progress Screen Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun ProgressScreenPreview() {
    CodeQuizTheme {
        ProgressScreenContent(
            uiState = ProgressUiState.Success(
                stats = UserProgressStats(
                    streak = Streak(
                        currentStreak = 3,
                        maxStreak = 7,
                        lastActiveDate = "2026-09-20",
                        isActiveToday = true
                    ),
                    totalXp = 240,
                    totalExercisesCompleted = 25,
                    completedLevelsCount = 2,
                    weekStreak = listOf(
                        WeekDayStatus("L", 15, "2026-09-15", isCompleted = true, isToday = false, isFuture = false),
                        WeekDayStatus("M", 16, "2026-09-16", isCompleted = true, isToday = false, isFuture = false),
                        WeekDayStatus("M", 17, "2026-09-17", isCompleted = false, isToday = false, isFuture = false),
                        WeekDayStatus("J", 18, "2026-09-18", isCompleted = true, isToday = false, isFuture = false),
                        WeekDayStatus("V", 19, "2026-09-19", isCompleted = true, isToday = false, isFuture = false),
                        WeekDayStatus("S", 20, "2026-09-20", isCompleted = true, isToday = true, isFuture = false),
                        WeekDayStatus("D", 21, "2026-09-21", isCompleted = false, isToday = false, isFuture = true)
                    ),
                    activeDatesThisMonth = setOf("2026-09-15", "2026-09-16", "2026-09-18", "2026-09-19", "2026-09-20")
                )
            )
        )
    }
}
