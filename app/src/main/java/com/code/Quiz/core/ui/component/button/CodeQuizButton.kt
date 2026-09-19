package com.code.Quiz.core.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.theme.spacing

enum class CodeQuizButtonVariant {
    Primary,
    Secondary,
    Ghost
}

@Composable
fun CodeQuizButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CodeQuizButtonVariant = CodeQuizButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val buttonShape = RoundedCornerShape(12.dp)
    val buttonMinHeightModifier = Modifier.defaultMinSize(minHeight = 48.dp)
    val contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm)

    val isInteractive = enabled && !isLoading

    when (variant) {
        CodeQuizButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.then(buttonMinHeightModifier),
                enabled = isInteractive,
                shape = buttonShape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = TextPrimary,
                    disabledContainerColor = AccentPurple.copy(alpha = 0.35f),
                    disabledContentColor = TextPrimary.copy(alpha = 0.5f)
                )
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = TextPrimary,
                    leadingIcon = leadingIcon
                )
            }
        }

        CodeQuizButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.then(buttonMinHeightModifier),
                enabled = isInteractive,
                shape = buttonShape,
                contentPadding = contentPadding,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isInteractive) DarkSurfaceBorder else DarkSurfaceBorder.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    disabledContainerColor = DarkSurface.copy(alpha = 0.5f),
                    disabledContentColor = TextMuted
                )
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = AccentPurple,
                    leadingIcon = leadingIcon
                )
            }
        }

        CodeQuizButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.then(buttonMinHeightModifier),
                enabled = isInteractive,
                shape = buttonShape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = TextMuted
                )
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = AccentPurple,
                    leadingIcon = leadingIcon
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    indicatorColor: Color,
    leadingIcon: (@Composable () -> Unit)?
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = indicatorColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Preview(name = "CodeQuizButton Variants", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizButtonPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CodeQuizButton(
                text = "Primary Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CodeQuizButtonVariant.Primary
            )
            CodeQuizButton(
                text = "Primary Loading",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CodeQuizButtonVariant.Primary,
                isLoading = true
            )
            CodeQuizButton(
                text = "Secondary Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CodeQuizButtonVariant.Secondary
            )
            CodeQuizButton(
                text = "Secondary Disabled",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CodeQuizButtonVariant.Secondary,
                enabled = false
            )
            CodeQuizButton(
                text = "Ghost Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CodeQuizButtonVariant.Ghost
            )
        }
    }
}
