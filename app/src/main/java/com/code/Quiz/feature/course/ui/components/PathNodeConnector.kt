package com.code.Quiz.feature.course.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary

/**
 * Displays the numbered step node in a glowing circular badge with vertical connecting
 * lines linking adjacent course cards in the roadmap.
 */
@Composable
fun PathNodeConnector(
    step: Int,
    modifier: Modifier = Modifier,
    isUnlocked: Boolean = true,
    showTopLine: Boolean = true,
    showBottomLine: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showTopLine) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(Spacing.md)
                    .background(DarkSurfaceBorder)
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .drawBehind {
                    drawCircle(
                        color = AccentPurple.copy(alpha = if (isUnlocked) 0.18f else 0.05f),
                        radius = size.minDimension / 2f + 4.dp.toPx()
                    )
                }
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(BorderStroke(1.5.dp, DarkSurfaceBorder), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        if (showBottomLine) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(Spacing.md)
                    .background(DarkSurfaceBorder)
            )
        }
    }
}

@Preview(name = "PathNodeConnector Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun PathNodeConnectorPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PathNodeConnector(step = 1, showTopLine = false, showBottomLine = true)
            PathNodeConnector(step = 2, showTopLine = true, showBottomLine = true)
            PathNodeConnector(step = 3, showTopLine = true, showBottomLine = false)
        }
    }
}
