package com.code.Quiz.core.ui.component.code

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.CodeBlockBackground
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.theme.spacing

@Composable
fun CodeSnippetBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String? = null,
    showLineNumbers: Boolean = false
) {
    val blockShape = RoundedCornerShape(12.dp)
    val horizontalScrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = blockShape,
        color = CodeBlockBackground,
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column {
            if (language != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated.copy(alpha = 0.5f))
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Terminal window dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(DarkSurfaceBorder, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(DarkSurfaceBorder, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(DarkSurfaceBorder, CircleShape)
                        )
                    }

                    // Language Chip
                    Box(
                        modifier = Modifier
                            .background(
                                color = DarkSurfaceElevated,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = DarkSurfaceBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = language.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = DarkSurfaceBorder
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .padding(MaterialTheme.spacing.md)
            ) {
                if (showLineNumbers) {
                    val lines = code.lines()
                    Row {
                        Column {
                            lines.indices.forEach { index ->
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp,
                                        color = TextMuted
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                        Text(
                            text = code,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = TextPrimary
                            )
                        )
                    }
                } else {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = TextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Preview(name = "CodeSnippetBlock Previews", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeSnippetBlockPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CodeSnippetBlock(
                language = "Kotlin",
                showLineNumbers = true,
                code = """
                    fun calculateStreak(completedDays: List<LocalDate>): Int {
                        return completedDays
                            .distinct()
                            .sortedDescending()
                            .fold(0) { acc, date -> acc + 1 }
                    }
                """.trimIndent()
            )

            CodeSnippetBlock(
                code = "val answer = list.find { it.id == selectedId }?.title ?: \"Unknown\""
            )
        }
    }
}
