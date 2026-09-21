package com.code.Quiz.core.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary

/**
 * Standardized circular icon button component for navigation and actions across CodeQuiz.
 * Enforces visual consistency with dark theme tokens, circular shape, and ripple effects.
 */
@Composable
fun CodeQuizIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    enabled: Boolean = true
) {
    val buttonShape = CircleShape
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(buttonShape)
            .background(DarkSurfaceElevated)
            .border(BorderStroke(1.dp, DarkSurfaceBorder), buttonShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = AccentCyan),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Standardized navigation back button using official [R.drawable.ic_arrow_back].
 */
@Composable
fun CodeQuizBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Volver"
) {
    CodeQuizIconButton(
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = contentDescription,
        onClick = onClick,
        tint = TextPrimary,
        modifier = modifier
    )
}

/**
 * Standardized modal dismiss / close button using official [R.drawable.ic_close].
 */
@Composable
fun CodeQuizCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Cerrar"
) {
    CodeQuizIconButton(
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = contentDescription,
        onClick = onClick,
        tint = TextSecondary,
        modifier = modifier
    )
}

@Preview(name = "CodeQuiz Nav Buttons Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizNavButtonsPreview() {
    CodeQuizTheme {
        Row(
            modifier = Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeQuizBackButton(onClick = {})
            CodeQuizCloseButton(onClick = {})
        }
    }
}
