package com.code.Quiz.feature.course.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.model.ExerciseDifficulty
import com.code.Quiz.core.ui.component.badge.BadgeVariant
import com.code.Quiz.core.ui.component.badge.CodeQuizBadge
import com.code.Quiz.core.ui.component.badge.DifficultyBadge
import com.code.Quiz.core.ui.component.button.ButtonVariant
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus

/**
 * Bottom Sheet displaying detailed information about a selected course level.
 * Shows level status badge, mascot in thinking/idle state, learning stats,
 * and context-aware action buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelDetailBottomSheet(
    level: CourseLevel,
    onDismiss: () -> Unit,
    onStartLevel: (levelId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = DarkSurface,
        scrimColor = DarkBackground.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.sm)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DarkSurfaceBorder)
            )
        }
    ) {
        LevelDetailContent(
            level = level,
            onDismiss = onDismiss,
            onStartLevel = onStartLevel,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
        )
    }
}

/**
 * Stateless content presentation for level details.
 */
@Composable
fun LevelDetailContent(
    level: CourseLevel,
    onDismiss: () -> Unit,
    onStartLevel: (levelId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status indicator badge
        when (level.status) {
            LevelStatus.COMPLETED -> {
                CodeQuizBadge(
                    text = "COMPLETADO ✓",
                    variant = BadgeVariant.Tag,
                    customTextColor = SuccessMint,
                    customBackgroundColor = DarkSurfaceElevated
                )
            }

            LevelStatus.ACTIVE -> {
                CodeQuizBadge(
                    text = "¡Tu reto actual!",
                    variant = BadgeVariant.Highlight
                )
            }

            LevelStatus.LOCKED -> {
                CodeQuizBadge(
                    text = "Nivel Bloqueado 🔒",
                    variant = BadgeVariant.Tag,
                    customTextColor = TextMuted,
                    customBackgroundColor = DarkSurfaceElevated
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Mascot thinking / idle
        CodeQuizMascot(
            state = if (level.status == LevelStatus.LOCKED) MascotState.IDLE else MascotState.THINKING,
            modifier = Modifier.size(110.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Level Title
        Text(
            text = "Nivel ${level.levelNumber}: ${level.title}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Level Description
        Text(
            text = level.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Level stats metrics
        CodeQuizCard(
            variant = CodeQuizCardVariant.Elevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Micro-retos
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${level.totalExercises}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "Micro-retos",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(DarkSurfaceBorder)
                )

                // XP Reward
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+${level.xpReward}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessMint
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "XP Recompensa",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(DarkSurfaceBorder)
                )

                // Estimated time
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "~${level.totalExercises * 2} min",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = "Tiempo estimado",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Progressive Difficulty Curve Info Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceElevated.copy(alpha = 0.6f))
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "Curva pedagógica del nivel:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                DifficultyBadge(difficulty = ExerciseDifficulty.EASY)
                Text(text = "➔", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                DifficultyBadge(difficulty = ExerciseDifficulty.MEDIUM)
                Text(text = "➔", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                DifficultyBadge(difficulty = ExerciseDifficulty.HARD)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Action area or locked warning
        if (level.status == LevelStatus.LOCKED) {
            CodeQuizCard(
                variant = CodeQuizCardVariant.Standard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Bloqueado",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Nivel Bloqueado. Debes completar los niveles anteriores para desbloquear este reto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            CodeQuizButton(
                text = "Entendido",
                variant = ButtonVariant.Secondary,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            val buttonText = if (level.status == LevelStatus.COMPLETED) {
                "Repasar Nivel ▶"
            } else {
                "¡Comenzar Nivel! ▶"
            }

            CodeQuizButton(
                text = buttonText,
                variant = ButtonVariant.Primary,
                onClick = { onStartLevel(level.id, level.title) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))
    }
}

@Preview(name = "LevelDetailBottomSheet Active Preview", showBackground = true, backgroundColor = 0xFF0A1124)
@Composable
private fun LevelDetailActivePreview() {
    CodeQuizTheme {
        LevelDetailContent(
            level = CourseLevel(
                id = "intro_prog_level_3",
                courseId = "intro_programming",
                levelNumber = 3,
                title = "Estructuras Condicionales",
                description = "Toma decisiones en tu código evaluando expresiones booleanas con if, else y when.",
                status = LevelStatus.ACTIVE,
                totalExercises = 6,
                xpReward = 40
            ),
            onDismiss = {},
            onStartLevel = { _, _ -> }
        )
    }
}

@Preview(name = "LevelDetailBottomSheet Locked Preview", showBackground = true, backgroundColor = 0xFF0A1124)
@Composable
private fun LevelDetailLockedPreview() {
    CodeQuizTheme {
        LevelDetailContent(
            level = CourseLevel(
                id = "intro_prog_level_4",
                courseId = "intro_programming",
                levelNumber = 4,
                title = "Bucles y Ciclos",
                description = "Repite instrucciones eficientemente con bucles while y for.",
                status = LevelStatus.LOCKED,
                totalExercises = 6,
                xpReward = 45
            ),
            onDismiss = {},
            onStartLevel = { _, _ -> }
        )
    }
}
