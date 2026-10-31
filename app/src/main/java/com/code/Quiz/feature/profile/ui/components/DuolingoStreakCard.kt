package com.code.Quiz.feature.profile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Duolingo-style streak showcase card featuring active streak metrics,
 * daily completion status pill, 7-day week streak row, and an expandable month calendar grid.
 */
@Composable
fun DuolingoStreakCard(
    stats: UserProgressStats,
    modifier: Modifier = Modifier
) {
    var isCalendarExpanded by remember { mutableStateOf(false) }

    CodeQuizCard(
        variant = CodeQuizCardVariant.Elevated,
        border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Big Flame Icon + Bold Streak Number + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔥",
                    fontSize = 38.sp
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = stats.streak.currentStreak.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = StreakOrange
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Días de racha",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = if (stats.streak.isActiveToday) "¡Objetivo diario cumplido!" else "¡Continúa tu hábito hoy!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Status Pill
            StreakStatusPill(isActiveToday = stats.streak.isActiveToday)

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 7-Day Week Streak Row
            WeekStreakRow(weekStreak = stats.weekStreak)

            Spacer(modifier = Modifier.height(Spacing.md))

            // Month Calendar Toggle Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = StreakOrange),
                        onClick = { isCalendarExpanded = !isCalendarExpanded }
                    )
                    .padding(vertical = Spacing.xs, horizontal = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calendario de actividad",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextSecondary
                )
                Text(
                    text = if (isCalendarExpanded) "Ocultar ▲" else "Ver mes ▼",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = StreakOrange
                )
            }

            // Expandable Month Calendar Grid
            AnimatedVisibility(
                visible = isCalendarExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm)
                ) {
                    MonthCalendarGrid(activeDates = stats.activeDatesThisMonth)
                }
            }
        }
    }
}

@Composable
private fun StreakStatusPill(
    isActiveToday: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActiveToday) {
        SuccessMint.copy(alpha = 0.12f)
    } else {
        DarkSurfaceElevated
    }

    val borderColor = if (isActiveToday) {
        SuccessMint.copy(alpha = 0.5f)
    } else {
        StreakOrange.copy(alpha = 0.5f)
    }

    val text = if (isActiveToday) {
        "¡Racha asegurada por hoy! 🔥"
    } else {
        "¡En juego! Practica hoy para mantenerla"
    }

    val textColor = if (isActiveToday) SuccessMint else StreakOrange

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeekStreakRow(
    weekStreak: List<WeekDayStatus>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        weekStreak.forEach { day ->
            WeekDayNode(day = day)
        }
    }
}

@Composable
private fun WeekDayNode(
    day: WeekDayStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day of week label (L, M, M, J, V, S, D)
        Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (day.isToday) StreakOrange else TextSecondary
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Node Circle
        val circleSize = 36.dp
        when {
            day.isCompleted -> {
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .background(StreakOrange.copy(alpha = 0.18f), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.5.dp, StreakOrange),
                        modifier = Modifier.size(circleSize)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🔥",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            day.isToday -> {
                // Today pending practice
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .background(DarkSurfaceElevated, CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(2.dp, StreakOrange),
                        modifier = Modifier.size(circleSize)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "HOY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = StreakOrange
                            )
                        }
                    }
                }
            }
            else -> {
                // Inactive or Future
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .background(DarkSurfaceElevated, CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, DarkSurfaceBorder),
                        modifier = Modifier.size(circleSize)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Sub-label for day of month
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (day.isToday) StreakOrange else TextMuted
        )
    }
}

@Composable
private fun MonthCalendarGrid(
    activeDates: Set<String>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val spanishLocale = Locale.forLanguageTag("es-ES")
    val monthName = today.month.getDisplayName(TextStyle.FULL, spanishLocale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(spanishLocale) else it.toString() }
    val year = today.year

    val firstDayOfMonth = today.withDayOfMonth(1)
    val dayOfWeekOfFirst = firstDayOfMonth.dayOfWeek.value // 1 (Mon) .. 7 (Sun)
    val leadingBlanks = dayOfWeekOfFirst - 1
    val totalDays = today.lengthOfMonth()

    val dayHeaders = listOf("L", "M", "M", "J", "V", "S", "D")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(Spacing.sm)
    ) {
        // Month title
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xs)
        )

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Grid cells in rows of 7
        val totalCells = leadingBlanks + totalDays
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - leadingBlanks + 1

                    if (cellIndex < leadingBlanks || dayNumber > totalDays) {
                        Spacer(modifier = Modifier.width(32.dp))
                    } else {
                        val dateStr = String.format(Locale.ROOT, "%04d-%02d-%02d", year, today.monthValue, dayNumber)
                        val isActive = activeDates.contains(dateStr)
                        val isCurrentDay = dayNumber == today.dayOfMonth

                        val cellBackground = when {
                            isActive -> StreakOrange
                            isCurrentDay -> StreakOrange.copy(alpha = 0.15f)
                            else -> Color.Transparent
                        }

                        val cellBorder = when {
                            isCurrentDay && !isActive -> BorderStroke(1.dp, StreakOrange)
                            else -> null
                        }

                        val textColor = when {
                            isActive -> TextPrimary
                            isCurrentDay -> StreakOrange
                            else -> TextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(cellBackground)
                                .then(
                                    if (cellBorder != null) {
                                        Modifier.background(cellBackground, RoundedCornerShape(6.dp))
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cellBorder != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Transparent,
                                    border = cellBorder,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = dayNumber.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = textColor
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isActive || isCurrentDay) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "DuolingoStreakCard Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun DuolingoStreakCardPreview() {
    CodeQuizTheme {
        val sampleStats = UserProgressStats(
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

        DuolingoStreakCard(
            stats = sampleStats,
            modifier = Modifier.padding(16.dp)
        )
    }
}
