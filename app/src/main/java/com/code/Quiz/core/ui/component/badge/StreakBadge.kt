package com.code.Quiz.core.ui.component.badge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.StreakPillBackground
import com.code.Quiz.core.theme.spacing

@Composable
fun StreakBadge(
    streakCount: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val pillShape = RoundedCornerShape(20.dp)

    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(pillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = StreakOrange),
                onClick = onClick
            )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier.then(clickableModifier),
        shape = pillShape,
        color = StreakPillBackground,
        border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔥",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
            Text(
                text = streakCount.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = StreakOrange,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Preview(name = "StreakBadge Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun StreakBadgePreview() {
    CodeQuizTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakBadge(streakCount = 1)
            StreakBadge(streakCount = 7)
            StreakBadge(streakCount = 42, onClick = {})
        }
    }
}
