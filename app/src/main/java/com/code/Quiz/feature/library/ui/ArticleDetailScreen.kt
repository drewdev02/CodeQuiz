package com.code.Quiz.feature.library.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.BadgeVariant
import com.code.Quiz.core.ui.component.badge.CodeQuizBadge
import com.code.Quiz.core.ui.component.button.ButtonVariant
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.card.CodeQuizCardVariant
import com.code.Quiz.core.ui.component.code.CodeSnippetBlock
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBack: () -> Unit,
    onNavigateToLevel: (levelId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel = koinViewModel()
) {
    LaunchedEffect(articleId) {
        viewModel.loadArticle(articleId)
    }

    val uiState by viewModel.uiState.collectAsState()

    ArticleDetailContent(
        uiState = uiState,
        onBack = onBack,
        onNavigateToLevel = onNavigateToLevel,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@Composable
fun ArticleDetailContent(
    uiState: ArticleDetailUiState,
    onBack: () -> Unit,
    onNavigateToLevel: (levelId: String, title: String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        when (uiState) {
            is ArticleDetailUiState.Loading -> {
                // Top Bar with back button only while loading
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onClick = onBack)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentPurple,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            is ArticleDetailUiState.Error -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onClick = onBack)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    CodeQuizButton(
                        text = "Reintentar",
                        onClick = onRetry
                    )
                }
            }

            is ArticleDetailUiState.Success -> {
                val article = uiState.article

                // Top bar: Back button ←, category chip, reading time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onClick = onBack)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        CodeQuizBadge(
                            text = article.category.uppercase(),
                            variant = BadgeVariant.Tag
                        )

                        Text(
                            text = "⏱ ${article.readTimeMinutes} min",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextMuted
                        )
                    }
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.md)
                ) {
                    // Title (22sp bold TextPrimary)
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    // Summary (italic TextSecondary, 14sp)
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 20.sp
                        ),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    // Content blocks rendered dynamically
                    article.blocks.forEach { block ->
                        when (block) {
                            is ArticleBlock.Text -> {
                                Text(
                                    text = block.content,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                    color = TextPrimary
                                )
                            }

                            is ArticleBlock.Code -> {
                                CodeSnippetBlock(
                                    code = block.content,
                                    language = block.language.ifBlank { null }
                                )
                            }

                            is ArticleBlock.Note -> {
                                CodeQuizCard(
                                    variant = CodeQuizCardVariant.Elevated,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "💡",
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.width(Spacing.sm))
                                        Text(
                                            text = block.content,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                lineHeight = 20.sp
                                            ),
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))
                    }

                    // Bottom CTA
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (article.linkedLevelId != null) {
                        CodeQuizButton(
                            text = "¡Poner a prueba este concepto! ▶",
                            onClick = { onNavigateToLevel(article.linkedLevelId, article.title) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        CodeQuizButton(
                            text = "Volver a la Biblioteca",
                            variant = ButtonVariant.Secondary,
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(buttonShape)
            .background(DarkSurface)
            .border(BorderStroke(1.dp, DarkSurfaceBorder), buttonShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = AccentCyan),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "←",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )
    }
}

@Preview(name = "Article Detail Success Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun ArticleDetailSuccessPreview() {
    CodeQuizTheme {
        ArticleDetailContent(
            uiState = ArticleDetailUiState.Success(
                article = Article(
                    id = "art_variables_tipos",
                    title = "Variables y Espacios de Memoria",
                    category = "Fundamentos",
                    summary = "Aprende cómo un programa almacena datos en memoria RAM, inmutabilidad y tipos de datos.",
                    readTimeMinutes = 3,
                    linkedCourseId = "intro_programming",
                    linkedLevelId = "intro_programming_level_1",
                    blocks = listOf(
                        ArticleBlock.Text("Una variable es un contenedor con nombre que reserva un espacio en la memoria RAM."),
                        ArticleBlock.Code(
                            content = "val name = \"Kotlin\"\nprintln(name)",
                            language = "Kotlin"
                        ),
                        ArticleBlock.Note("Prefiere siempre val sobre var para inmutabilidad.")
                    )
                )
            ),
            onBack = {},
            onNavigateToLevel = { _, _ -> },
            onRetry = {}
        )
    }
}
