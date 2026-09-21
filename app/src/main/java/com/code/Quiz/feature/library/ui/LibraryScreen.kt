package com.code.Quiz.feature.library.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.R
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurface
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.DarkSurfaceElevated
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.BadgeVariant
import com.code.Quiz.core.ui.component.badge.CodeQuizBadge
import com.code.Quiz.core.ui.component.badge.StreakBadge
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.usecase.GetStreakUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LibraryScreen(
    onArticleClick: (articleId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = koinViewModel(),
    getStreakUseCase: GetStreakUseCase = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val streak by getStreakUseCase().collectAsState(initial = Streak(0, 0, null, false))

    LibraryScreenContent(
        uiState = uiState,
        streakCount = streak.currentStreak,
        onCategorySelect = viewModel::selectCategory,
        onArticleClick = onArticleClick,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@Composable
fun LibraryScreenContent(
    uiState: LibraryUiState,
    onCategorySelect: (category: String) -> Unit,
    onArticleClick: (articleId: String) -> Unit,
    onRetry: () -> Unit,
    streakCount: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top bar with "Biblioteca" in TextPrimary (18sp bold) and StreakBadge(streakCount = streakCount)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Biblioteca",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )

            StreakBadge(streakCount = streakCount)
        }

        when (uiState) {
            is LibraryUiState.Loading -> {
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

            is LibraryUiState.Error -> {
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

            is LibraryUiState.Success -> {
                // Filter Chips: horizontal scrolling Row of category chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    contentPadding = PaddingValues(horizontal = Spacing.md)
                ) {
                    items(uiState.categories) { category ->
                        val isSelected = category == uiState.selectedCategory
                        CategoryFilterChip(
                            text = category,
                            isSelected = isSelected,
                            onClick = { onCategorySelect(category) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                // List of articles using LazyColumn
                if (uiState.articles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(Spacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay artículos disponibles en esta categoría.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(
                            start = Spacing.md,
                            end = Spacing.md,
                            top = Spacing.sm,
                            bottom = Spacing.lg
                        )
                    ) {
                        items(uiState.articles, key = { it.id }) { article ->
                            ArticleCardItem(
                                article = article,
                                onClick = { onArticleClick(article.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipShape = RoundedCornerShape(20.dp)
    val backgroundColor = if (isSelected) DarkSurfaceElevated else DarkSurface
    val border = if (isSelected) {
        BorderStroke(1.dp, AccentCyan)
    } else {
        BorderStroke(1.dp, DarkSurfaceBorder)
    }
    val textColor = if (isSelected) AccentCyan else TextSecondary

    Box(
        modifier = modifier
            .clip(chipShape)
            .border(border, chipShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = AccentCyan),
                onClick = onClick
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            ),
            color = textColor
        )
    }
}

@Composable
private fun ArticleCardItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CodeQuizCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        // Top row: category badge with CodeQuizBadge and reading time "⏱ ${article.readTimeMinutes} min" in TextMuted
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Title in TextPrimary (18sp semi-bold)
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Summary in TextSecondary (14sp)
        Text(
            text = article.summary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = TextSecondary
        )

        // If article.linkedLevelId != null: subtle bottom row with ic_target and "Incluye micro-retos prácticos"
        if (article.linkedLevelId != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_target),
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "Incluye micro-retos prácticos",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextMuted
                )
            }
        }
    }
}

@Preview(name = "Library Screen Success Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun LibraryScreenSuccessPreview() {
    CodeQuizTheme {
        LibraryScreenContent(
            uiState = LibraryUiState.Success(
                categories = listOf("Todos", "Fundamentos", "Lógica", "Buenas Prácticas"),
                selectedCategory = "Todos",
                articles = listOf(
                    Article(
                        id = "art_1",
                        title = "Variables y Espacios de Memoria",
                        category = "Fundamentos",
                        summary = "Aprende cómo un programa almacena datos en memoria RAM, inmutabilidad y tipos de datos.",
                        readTimeMinutes = 3,
                        linkedCourseId = "intro_programming",
                        linkedLevelId = "intro_programming_level_1",
                        blocks = listOf(
                            ArticleBlock.Text("Texto de prueba")
                        )
                    ),
                    Article(
                        id = "art_2",
                        title = "Operadores y Evaluación Lógica",
                        category = "Fundamentos",
                        summary = "Operaciones aritméticas, operadores relacionales y evaluación de expresiones booleanas.",
                        readTimeMinutes = 4,
                        blocks = emptyList()
                    )
                )
            ),
            onCategorySelect = {},
            onArticleClick = {},
            onRetry = {}
        )
    }
}
