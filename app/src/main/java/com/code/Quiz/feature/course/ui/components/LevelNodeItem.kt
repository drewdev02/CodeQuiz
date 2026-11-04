package com.code.Quiz.feature.course.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.HeroGradient
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.BadgeVariant
import com.code.Quiz.core.ui.component.badge.CodeQuizBadge
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus

/**
 * Visual rendering of an individual level node in the gamified course roadmap path.
 * Features state-dependent circular node styling, continuous vertical connector lines,
 * companion mascot on the active level, and interactive card presentation.
 */
@Composable
fun LevelNodeItem(
    level: CourseLevel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrentChallenge: Boolean = level.status == LevelStatus.ACTIVE,
    showTopLine: Boolean = true,
    showBottomLine: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "activePulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val connectorLineColor = when (level.status) {
        LevelStatus.COMPLETED -> SuccessMint
        LevelStatus.ACTIVE -> AccentCyan
        LevelStatus.LOCKED -> DarkSurfaceBorder
    }

    val itemAlpha = if (level.status == LevelStatus.LOCKED) 0.65f else 1.0f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical connector and circular node container
        Box(
            modifier = Modifier
                .width(68.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            // Continuous vertical connector lines behind the circular node
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (showTopLine) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .weight(1f)
                            .background(connectorLineColor)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Space reserved for the centered 50.dp circular node
                Spacer(modifier = Modifier.height(50.dp))

                if (showBottomLine) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .weight(1f)
                            .background(connectorLineColor)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Circular Node Container (centered with the card)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .then(
                        if (isCurrentChallenge) {
                            Modifier.drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(AccentCyan.copy(alpha = glowAlpha), Color.Transparent),
                                        radius = size.minDimension * 0.95f
                                    )
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .clip(CircleShape)
                    .then(
                        when (level.status) {
                            LevelStatus.COMPLETED -> Modifier
                                .background(DarkSurface)
                                .border(BorderStroke(2.dp, SuccessMint), CircleShape)

                            LevelStatus.ACTIVE -> Modifier
                                .background(DarkSurface)
                                .border(
                                    if (isCurrentChallenge) BorderStroke(2.5.dp, HeroGradient)
                                    else BorderStroke(1.5.dp, AccentCyan),
                                    CircleShape
                                )

                            LevelStatus.LOCKED -> Modifier
                                .background(DarkSurfaceElevated)
                                .border(BorderStroke(1.5.dp, DarkSurfaceBorder), CircleShape)
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = if (level.status == LevelStatus.COMPLETED) SuccessMint else AccentCyan),
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (level.status) {
                    LevelStatus.COMPLETED -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Nivel completado",
                            tint = SuccessMint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    LevelStatus.ACTIVE -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play_arrow),
                            contentDescription = "Nivel activo",
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    LevelStatus.LOCKED -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Nivel bloqueado",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Mascot perched directly on top of the active circular node
            if (isCurrentChallenge) {
                CodeQuizMascot(
                    state = MascotState.IDLE,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-3).dp, y = (-43).dp)
                        .size(62.dp)
                        .zIndex(2f)
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        // Level Details Card
        CodeQuizCard(
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = Spacing.xs),
            variant = if (isCurrentChallenge) CodeQuizCardVariant.Elevated else CodeQuizCardVariant.Standard,
            border = if (isCurrentChallenge) {
                BorderStroke(1.5.dp, AccentPurple.copy(alpha = 0.8f))
            } else {
                BorderStroke(1.dp, DarkSurfaceBorder)
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top header: Status Badge on left, XP Badge on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCurrentChallenge) {
                        CodeQuizBadge(
                            text = "¡Tu reto actual!",
                            variant = BadgeVariant.Highlight
                        )
                    } else {
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
                                    text = "DISPONIBLE",
                                    variant = BadgeVariant.Tag,
                                    customTextColor = AccentCyan,
                                    customBackgroundColor = DarkSurfaceElevated
                                )
                            }

                            LevelStatus.LOCKED -> {
                                CodeQuizBadge(
                                    text = "BLOQUEADO",
                                    variant = BadgeVariant.Tag,
                                    customTextColor = TextMuted,
                                    customBackgroundColor = DarkSurfaceElevated
                                )
                            }
                        }
                    }

                    CodeQuizBadge(
                        text = "+${level.xpReward} XP",
                        variant = BadgeVariant.Tag,
                        customTextColor = when (level.status) {
                            LevelStatus.COMPLETED -> SuccessMint
                            LevelStatus.ACTIVE -> AccentCyan
                            LevelStatus.LOCKED -> TextMuted
                        },
                        customBackgroundColor = DarkSurfaceElevated
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Title - 100% full width without being squished!
                Text(
                    text = "Nivel ${level.levelNumber}: ${level.title}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isCurrentChallenge) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = if (level.status == LevelStatus.LOCKED) TextSecondary else TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Description
                Text(
                    text = level.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (level.status == LevelStatus.LOCKED) TextMuted else TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Bottom row: Target icon + micro-retos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_target),
                        contentDescription = null,
                        tint = if (level.status == LevelStatus.LOCKED) TextMuted else AccentCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${level.totalExercises} micro-retos",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (level.status == LevelStatus.LOCKED) TextMuted else TextSecondary
                    )
                }
            }
        }
    }
}

@Preview(name = "LevelNodeItem Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun LevelNodeItemPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth()
        ) {
            LevelNodeItem(
                level = CourseLevel(
                    id = "lvl_1",
                    courseId = "intro",
                    levelNumber = 1,
                    title = "Variables y Tipos",
                    description = "Aprende qué es una variable y cómo almacenar datos.",
                    status = LevelStatus.COMPLETED,
                    totalExercises = 5,
                    xpReward = 30
                ),
                onClick = {},
                showTopLine = false,
                showBottomLine = true
            )

            LevelNodeItem(
                level = CourseLevel(
                    id = "lvl_2",
                    courseId = "intro",
                    levelNumber = 2,
                    title = "Estructuras Condicionales",
                    description = "Toma decisiones en tu código evaluando expresiones con if y else.",
                    status = LevelStatus.ACTIVE,
                    totalExercises = 6,
                    xpReward = 40
                ),
                onClick = {},
                showTopLine = true,
                showBottomLine = true
            )

            LevelNodeItem(
                level = CourseLevel(
                    id = "lvl_3",
                    courseId = "intro",
                    levelNumber = 3,
                    title = "Bucles y Ciclos",
                    description = "Repite instrucciones eficientemente con bucles while y for.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 6,
                    xpReward = 45
                ),
                onClick = {},
                showTopLine = true,
                showBottomLine = false
            )
        }
    }
}
