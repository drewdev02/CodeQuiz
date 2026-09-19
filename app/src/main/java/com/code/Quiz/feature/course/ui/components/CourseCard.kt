package com.code.Quiz.feature.course.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.BadgeVariant
import com.code.Quiz.core.ui.component.badge.CodeQuizBadge
import com.code.Quiz.core.ui.component.button.ButtonVariant
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType

/**
 * Roadmap course card built strictly with core design system components
 * ([CodeQuizCard], [CodeQuizButton], [CodeQuizBadge], and [Spacing] tokens).
 */
@Composable
fun CourseCard(
    course: Course,
    onCourseClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconRes, iconTint) = when (course.iconType) {
        CourseIconType.ROCKET -> R.drawable.ic_course_rocket to StreakOrange
        CourseIconType.CODE -> R.drawable.ic_course_code to AccentCyan
        CourseIconType.PYTHON -> R.drawable.ic_course_python to SuccessMint
        CourseIconType.DATABASE -> R.drawable.ic_course_database to AccentPurple
        CourseIconType.TERMINAL -> R.drawable.ic_course_terminal to AccentCyan
    }

    CodeQuizCard(
        modifier = modifier.fillMaxWidth()
    ) {
        // Top row with "Curso" tag and optional highlight badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeQuizBadge(
                text = "CURSO",
                variant = BadgeVariant.Tag
            )

            if (course.badge != null) {
                CodeQuizBadge(
                    text = course.badge,
                    variant = BadgeVariant.Highlight
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Circular themed icon container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(BorderStroke(1.dp, DarkSurfaceBorder), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = course.title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Course Title
        Text(
            text = course.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Course Description
        Text(
            text = course.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Action button
        CodeQuizButton(
            text = "Ir al curso →",
            variant = ButtonVariant.Secondary,
            onClick = { onCourseClick(course) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "CourseCard Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CourseCardPreview() {
    CodeQuizTheme {
        Box(modifier = Modifier.padding(Spacing.md)) {
            CourseCard(
                course = Course(
                    id = "intro_programming",
                    title = "Curso de introducción a la programación",
                    description = "Aprende los fundamentos de la computación, pensamiento lógico, variables y estructuras de control.",
                    order = 1,
                    badge = "Curso Top",
                    iconType = CourseIconType.ROCKET,
                    isUnlocked = true
                ),
                onCourseClick = {}
            )
        }
    }
}
