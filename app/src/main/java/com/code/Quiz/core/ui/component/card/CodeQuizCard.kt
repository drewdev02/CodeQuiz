package com.code.Quiz.core.ui.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.theme.spacing

enum class CodeQuizCardVariant {
    Standard,
    Elevated
}

@Composable
fun CodeQuizCard(
    modifier: Modifier = Modifier,
    variant: CodeQuizCardVariant = CodeQuizCardVariant.Standard,
    shape: Shape = RoundedCornerShape(16.dp),
    border: BorderStroke? = BorderStroke(1.dp, DarkSurfaceBorder),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val containerColor = when (variant) {
        CodeQuizCardVariant.Standard -> DarkSurface
        CodeQuizCardVariant.Elevated -> DarkSurfaceElevated
    }

    val cardColors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = TextPrimary,
        disabledContainerColor = containerColor.copy(alpha = 0.5f),
        disabledContentColor = TextSecondary.copy(alpha = 0.5f)
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = cardColors,
            border = border
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            border = border
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                content()
            }
        }
    }
}

@Preview(name = "CodeQuizCard Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizCardPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CodeQuizCard(
                variant = CodeQuizCardVariant.Standard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Standard Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Standard card using DarkSurface with 1dp DarkSurfaceBorder.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            CodeQuizCard(
                variant = CodeQuizCardVariant.Elevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Elevated Metric Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Elevated card using DarkSurfaceElevated for metric and stat items.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            CodeQuizCard(
                variant = CodeQuizCardVariant.Standard,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Clickable Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Clickable card with ripple and hover feedback.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
