package com.code.Quiz.core.ui.component.code

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.spacing

/**
 * Modern developer terminal-styled code block component for CodeQuiz.
 *
 * Features:
 * - Native syntax highlighting powered by [CodeSyntaxHighlighter]
 * - Responsive mobile formatting that avoids horizontal overflow
 * - Terminal window header with subtle macOS-style window dots and language badge
 * - Optional line numbers support
 * - Clean dark theme styling adhering to DESIGN.md
 */
@Composable
fun CodeSnippetBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String? = null,
    showLineNumbers: Boolean = false
) {
    val blockShape = RoundedCornerShape(12.dp)

    val formattedCode = remember(code) {
        CodeSyntaxHighlighter.formatForMobile(code)
    }
    val highlightedCode = remember(formattedCode, language) {
        CodeSyntaxHighlighter.highlight(formattedCode, language)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = blockShape,
        color = CodeBlockBackground,
        border = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column {
            // Terminal window header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated.copy(alpha = 0.5f))
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Window controls (traffic light dots with subtle opacity)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF5F56).copy(alpha = 0.7f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFFBD2E).copy(alpha = 0.7f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF27C93F).copy(alpha = 0.7f), CircleShape)
                    )
                }

                // Language Chip
                if (!language.isNullOrBlank()) {
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
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = DarkSurfaceBorder
            )

            // Code Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md)
            ) {
                if (showLineNumbers) {
                    val lineCount = remember(formattedCode) { formattedCode.lines().size }
                    val lineNumbersText = remember(lineCount) {
                        (1..lineCount).joinToString("\n")
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = lineNumbersText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                color = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                        Text(
                            text = highlightedCode,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                color = TextPrimary
                            ),
                            softWrap = true
                        )
                    }
                } else {
                    Text(
                        text = highlightedCode,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = TextPrimary
                        ),
                        softWrap = true
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
            // Sample 1: Kotlin if-else expression (from user screenshot)
            CodeSnippetBlock(
                code = "val status = if (isOnline) \"Conectado\" else \"Desconectado\"",
                language = "Kotlin"
            )

            // Sample 2: Function with variables, types and line numbers
            CodeSnippetBlock(
                language = "Kotlin",
                showLineNumbers = true,
                code = """
                    fun calculateStreak(completedDays: List<LocalDate>): Int {
                        val activeDays = completedDays.distinct()
                        return if (activeDays.isNotEmpty()) activeDays.size else 0
                    }
                """.trimIndent()
            )

            // Sample 3: String manipulation, lambdas, and operators
            CodeSnippetBlock(
                language = "Kotlin",
                code = """
                    val result = list.filter { it > 10 }
                        .map { "Score: ${'$'}it" }
                        .joinToString(", ")
                """.trimIndent()
            )

            // Sample 4: Snippet without language header
            CodeSnippetBlock(
                code = "val answer = list.find { it.id == selectedId }?.title ?: \"Unknown\""
            )
        }
    }
}
