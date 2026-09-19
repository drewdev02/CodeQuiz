package com.code.Quiz.core.ui.component.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeBlockBackground
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.ErrorRed
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.theme.spacing

enum class OptionSelectionState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT
}

enum class OptionIndicatorType {
    SINGLE_CHOICE,
    MULTI_CHOICE
}

@Composable
fun CodeQuizOptionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: OptionSelectionState = OptionSelectionState.DEFAULT,
    indicatorType: OptionIndicatorType = OptionIndicatorType.SINGLE_CHOICE,
    label: String? = null,
    codeSnippet: String? = null,
    enabled: Boolean = true
) {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionSelectionState.DEFAULT -> DarkSurfaceBorder
            OptionSelectionState.SELECTED -> AccentPurple
            OptionSelectionState.CORRECT -> SuccessMint
            OptionSelectionState.INCORRECT -> ErrorRed
        },
        animationSpec = tween(durationMillis = 200),
        label = "OptionBorderColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            OptionSelectionState.DEFAULT -> DarkSurface
            OptionSelectionState.SELECTED -> DarkSurface.copy(alpha = 0.85f).compositeOver(AccentPurple.copy(alpha = 0.12f))
            OptionSelectionState.CORRECT -> DarkSurface.copy(alpha = 0.85f).compositeOver(SuccessMint.copy(alpha = 0.12f))
            OptionSelectionState.INCORRECT -> DarkSurface.copy(alpha = 0.85f).compositeOver(ErrorRed.copy(alpha = 0.12f))
        },
        animationSpec = tween(durationMillis = 200),
        label = "OptionBackgroundColor"
    )

    val borderWidth = if (state == OptionSelectionState.DEFAULT) 1.dp else 1.5.dp
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clip(cardShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = borderColor),
                enabled = enabled,
                role = if (indicatorType == OptionIndicatorType.SINGLE_CHOICE) Role.RadioButton else Role.Checkbox,
                onClick = onClick
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = TextPrimary
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OptionIndicator(
                state = state,
                indicatorType = indicatorType
            )

            if (label != null) {
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (state) {
                            OptionSelectionState.DEFAULT -> TextSecondary
                            OptionSelectionState.SELECTED -> AccentPurple
                            OptionSelectionState.CORRECT -> SuccessMint
                            OptionSelectionState.INCORRECT -> ErrorRed
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary,
                        fontWeight = if (state != OptionSelectionState.DEFAULT) FontWeight.Medium else FontWeight.Normal
                    )
                )

                if (codeSnippet != null) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = CodeBlockBackground,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = DarkSurfaceBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = codeSnippet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionIndicator(
    state: OptionSelectionState,
    indicatorType: OptionIndicatorType,
    modifier: Modifier = Modifier
) {
    val indicatorSize = 22.dp

    when (indicatorType) {
        OptionIndicatorType.SINGLE_CHOICE -> {
            when (state) {
                OptionSelectionState.DEFAULT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .border(1.5.dp, DarkSurfaceBorder, CircleShape)
                    )
                }

                OptionSelectionState.SELECTED -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .border(2.dp, AccentPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(AccentPurple, CircleShape)
                        )
                    }
                }

                OptionSelectionState.CORRECT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .background(SuccessMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.52f)
                                lineTo(size.width * 0.44f, size.height * 0.74f)
                                lineTo(size.width * 0.8f, size.height * 0.26f)
                            }
                            drawPath(
                                path = path,
                                color = DarkBackground,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                OptionSelectionState.INCORRECT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .background(ErrorRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawLine(
                                color = TextPrimary,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = TextPrimary,
                                start = Offset(size.width, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        OptionIndicatorType.MULTI_CHOICE -> {
            val checkboxShape = RoundedCornerShape(6.dp)
            when (state) {
                OptionSelectionState.DEFAULT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .border(1.5.dp, DarkSurfaceBorder, checkboxShape)
                    )
                }

                OptionSelectionState.SELECTED -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .background(AccentPurple, checkboxShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.52f)
                                lineTo(size.width * 0.44f, size.height * 0.74f)
                                lineTo(size.width * 0.8f, size.height * 0.26f)
                            }
                            drawPath(
                                path = path,
                                color = TextPrimary,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                OptionSelectionState.CORRECT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .background(SuccessMint, checkboxShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.52f)
                                lineTo(size.width * 0.44f, size.height * 0.74f)
                                lineTo(size.width * 0.8f, size.height * 0.26f)
                            }
                            drawPath(
                                path = path,
                                color = DarkBackground,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                OptionSelectionState.INCORRECT -> {
                    Box(
                        modifier = modifier
                            .size(indicatorSize)
                            .background(ErrorRed, checkboxShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawLine(
                                color = TextPrimary,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = TextPrimary,
                                start = Offset(size.width, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
    }
}

// Utility extension to composite colors cleanly
private fun Color.compositeOver(background: Color): Color {
    val bgAlpha = background.alpha
    val fgAlpha = this.alpha
    val a = fgAlpha + bgAlpha * (1f - fgAlpha)
    val r = (this.red * fgAlpha + background.red * bgAlpha * (1f - fgAlpha)) / a
    val g = (this.green * fgAlpha + background.green * bgAlpha * (1f - fgAlpha)) / a
    val b = (this.blue * fgAlpha + background.blue * bgAlpha * (1f - fgAlpha)) / a
    return Color(red = r, green = g, blue = b, alpha = a)
}

@Preview(name = "Single Choice Option States", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizOptionCardSinglePreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CodeQuizOptionCard(
                text = "Default Option State",
                label = "A",
                state = OptionSelectionState.DEFAULT,
                indicatorType = OptionIndicatorType.SINGLE_CHOICE,
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Selected Option State",
                label = "B",
                state = OptionSelectionState.SELECTED,
                indicatorType = OptionIndicatorType.SINGLE_CHOICE,
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Correct Validated Answer",
                label = "C",
                state = OptionSelectionState.CORRECT,
                indicatorType = OptionIndicatorType.SINGLE_CHOICE,
                codeSnippet = "val result = answers.filter { it.isCorrect }",
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Incorrect Validated Answer",
                label = "D",
                state = OptionSelectionState.INCORRECT,
                indicatorType = OptionIndicatorType.SINGLE_CHOICE,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Multi Choice Option States", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizOptionCardMultiPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CodeQuizOptionCard(
                text = "Default Checkbox Option",
                state = OptionSelectionState.DEFAULT,
                indicatorType = OptionIndicatorType.MULTI_CHOICE,
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Selected Checkbox Option",
                state = OptionSelectionState.SELECTED,
                indicatorType = OptionIndicatorType.MULTI_CHOICE,
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Correct Multi-choice Answer",
                state = OptionSelectionState.CORRECT,
                indicatorType = OptionIndicatorType.MULTI_CHOICE,
                onClick = {}
            )
            CodeQuizOptionCard(
                text = "Incorrect Multi-choice Answer",
                state = OptionSelectionState.INCORRECT,
                indicatorType = OptionIndicatorType.MULTI_CHOICE,
                onClick = {}
            )
        }
    }
}
