package com.code.Quiz.feature.course.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.StreakBadge
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType
import com.code.Quiz.feature.course.ui.components.CourseCard
import com.code.Quiz.feature.course.ui.components.PathNodeConnector
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful entry point for the Course Roadmap Path screen.
 * Injected with [CourseViewModel] via Koin.
 */
@Composable
fun CoursePathScreen(
    onCourseSelected: (courseId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CoursePathContent(
        uiState = uiState,
        onCourseSelected = onCourseSelected,
        modifier = modifier
    )
}

/**
 * Stateless content composable for the Course Roadmap Path screen.
 */
@Composable
fun CoursePathContent(
    uiState: CourseUiState,
    onCourseSelected: (courseId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            ) {
                Text(
                    text = "Ruta de Aprendizaje",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

                StreakBadge(
                    streakCount = 1,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is CourseUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = AccentPurple,
                        strokeWidth = 3.dp
                    )
                }
            }

            is CourseUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CourseUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = Spacing.lg,
                        end = Spacing.lg,
                        top = Spacing.xs,
                        bottom = Spacing.xxl
                    )
                ) {
                    itemsIndexed(
                        items = uiState.courses,
                        key = { _, course -> course.id }
                    ) { index, course ->
                        PathNodeConnector(
                            step = course.order,
                            isUnlocked = course.isUnlocked,
                            showTopLine = index > 0,
                            showBottomLine = true
                        )

                        CourseCard(
                            course = course,
                            onCourseClick = { selectedCourse ->
                                onCourseSelected(selectedCourse.id, selectedCourse.title)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "CoursePathScreen Success Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CoursePathScreenPreview() {
    CodeQuizTheme {
        CoursePathContent(
            uiState = CourseUiState.Success(
                courses = listOf(
                    Course(
                        id = "intro_programming",
                        title = "Curso de introducción a la programación",
                        description = "Aprende los fundamentos de la computación, pensamiento lógico, variables y estructuras de control.",
                        order = 1,
                        badge = "Curso Top",
                        iconType = CourseIconType.ROCKET,
                        isUnlocked = true
                    ),
                    Course(
                        id = "kotlin_basics",
                        title = "Fundamentos de Kotlin",
                        description = "Sintaxis moderna, inmutabilidad, funciones, clases selladas y null safety sin rodeos.",
                        order = 2,
                        badge = "Recomendado",
                        iconType = CourseIconType.CODE,
                        isUnlocked = true
                    ),
                    Course(
                        id = "data_structures",
                        title = "Estructuras de Datos Básicas",
                        description = "Listas, pilas, colas, mapas y árboles explicados con retos visuales e interactivos.",
                        order = 3,
                        badge = null,
                        iconType = CourseIconType.DATABASE,
                        isUnlocked = true
                    ),
                    Course(
                        id = "algorithms_logic",
                        title = "Algoritmos y Resolución de Problemas",
                        description = "Búsquedas, ordenamientos y análisis de complejidad de forma práctica.",
                        order = 4,
                        badge = "Avanzado",
                        iconType = CourseIconType.TERMINAL,
                        isUnlocked = true
                    )
                )
            ),
            onCourseSelected = { _, _ -> }
        )
    }
}
