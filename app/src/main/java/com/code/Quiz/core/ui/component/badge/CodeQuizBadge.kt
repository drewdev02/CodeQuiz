package com.code.Quiz.core.ui.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.model.ExerciseDifficulty

enum class BadgeVariant {
    Tag,
    Highlight
}

/**
 * Design system badge chip component for tags, status indicators, and course highlight chips.
 */
@Composable
fun CodeQuizBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Tag,
    customBackgroundColor: Color? = null,
    customTextColor: Color? = null
) {
    val shape = RoundedCornerShape(6.dp)

    val backgroundColor = customBackgroundColor ?: when (variant) {
        BadgeVariant.Tag -> DarkSurfaceElevated
        BadgeVariant.Highlight -> Color(0xFFFFB800)
    }

    val textColor = customTextColor ?: when (variant) {
        BadgeVariant.Tag -> AccentCyan
        BadgeVariant.Highlight -> DarkBackground
    }

    val borderModifier = when (variant) {
        BadgeVariant.Tag -> Modifier.border(1.dp, DarkSurfaceBorder, shape)
        BadgeVariant.Highlight -> Modifier.border(1.dp, StreakOrange.copy(alpha = 0.5f), shape)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(borderModifier)
            .background(backgroundColor)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = if (variant == BadgeVariant.Tag) 1.sp else 0.2.sp
            )
        )
    }
}

/**
 * Pedagogical difficulty badge component for CodeQuiz micro-challenges.
 * Displays "FÁCIL" (SuccessMint), "MEDIO" (StreakOrange), or "DIFÍCIL" (AccentPurple).
 */
@Composable
fun DifficultyBadge(
    difficulty: ExerciseDifficulty,
    modifier: Modifier = Modifier
) {
    val (label, accentColor) = when (difficulty) {
        ExerciseDifficulty.EASY -> "FÁCIL" to SuccessMint
        ExerciseDifficulty.MEDIUM -> "MEDIO" to StreakOrange
        ExerciseDifficulty.HARD -> "DIFÍCIL" to AccentPurple
    }

    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, accentColor.copy(alpha = 0.45f), shape)
            .background(DarkSurfaceElevated)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 0.8.sp,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Preview(name = "CodeQuizBadge Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizBadgePreview() {
    CodeQuizTheme {
        Row(
            modifier = Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeQuizBadge(text = "CURSO", variant = BadgeVariant.Tag)
            CodeQuizBadge(text = "Curso Top", variant = BadgeVariant.Highlight)
            DifficultyBadge(difficulty = ExerciseDifficulty.EASY)
            DifficultyBadge(difficulty = ExerciseDifficulty.MEDIUM)
            DifficultyBadge(difficulty = ExerciseDifficulty.HARD)
        }
    }
}
