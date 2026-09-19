package com.code.Quiz.core.ui.component.progress

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.HeroGradient
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.theme.spacing

@Composable
fun CodeQuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    animated: Boolean = true,
    animationDurationMillis: Int = 600,
    trackColor: Color = DarkSurfaceElevated,
    brush: Brush = HeroGradient
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = if (animated) {
            tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing)
        } else {
            snap()
        },
        label = "CodeQuizProgressBarAnimation"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val trackCornerRadius = CornerRadius(size.height / 2f, size.height / 2f)

        // Draw background track
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = trackCornerRadius
        )

        // Draw animated progress with HeroGradient
        if (animatedProgress > 0f) {
            val progressWidth = size.width * animatedProgress
            val fillCornerRadiusX = (size.height / 2f).coerceAtMost(progressWidth / 2f)
            val fillCornerRadiusY = size.height / 2f

            drawRoundRect(
                brush = brush,
                size = Size(progressWidth, size.height),
                cornerRadius = CornerRadius(fillCornerRadiusX, fillCornerRadiusY)
            )
        }
    }
}

@Preview(name = "CodeQuizProgressBar Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizProgressBarPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Progress: 0%",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            CodeQuizProgressBar(progress = 0f)

            Text(
                text = "Progress: 35%",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            CodeQuizProgressBar(progress = 0.35f)

            Text(
                text = "Progress: 75%",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            CodeQuizProgressBar(progress = 0.75f)

            Text(
                text = "Progress: 100%",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            CodeQuizProgressBar(progress = 1f)
        }
    }
}
