package com.code.Quiz.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.badge.StreakBadge
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.button.CodeQuizButtonVariant
import com.code.Quiz.core.ui.component.card.CodeQuizCard
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import com.code.Quiz.feature.course.ui.CoursePathScreen
import com.code.Quiz.feature.onboarding.ui.OnboardingScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = OnboardingRoute,
        modifier = modifier
    ) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(HomeRoute) {
                        popUpTo<OnboardingRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<HomeRoute> {
            CoursePathScreen(
                onCourseSelected = { courseId, title ->
                    navController.navigate(QuizRoute(lessonId = courseId, title = title))
                }
            )
        }

        composable<QuizRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<QuizRoute>()
            QuizPlaceholderScreen(
                route = route,
                onExit = {
                    navController.navigate(HomeRoute) {
                        popUpTo<HomeRoute> {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onStartQuiz: (lessonId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Streak Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StreakBadge(streakCount = 1)
            }

            // Central Content: Mascot + Welcome Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CodeQuizMascot(
                    state = MascotState.IDLE,
                    modifier = Modifier.size(220.dp)
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(
                    text = "¡Listo para programar!",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = "Completa tu desafío diario y mantén tu racha activa.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Actions: Start Quiz Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CodeQuizButton(
                    text = "Iniciar Quiz",
                    onClick = {
                        onStartQuiz("lesson_1", "Introducción a Kotlin")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun QuizPlaceholderScreen(
    route: QuizRoute,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top area / Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (route.title.isNotBlank()) route.title else "Quiz",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = "Lección: ${route.lessonId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Center Mascot & Placeholder Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CodeQuizMascot(
                    state = MascotState.THINKING,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                CodeQuizCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "El contenido del quiz estará disponible próximamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Bottom Exit Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CodeQuizButton(
                    text = "Volver al Inicio",
                    onClick = onExit,
                    variant = CodeQuizButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Home Screen Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun HomeScreenPreview() {
    CodeQuizTheme {
        HomeScreen(onStartQuiz = { _, _ -> })
    }
}

@Preview(name = "Quiz Placeholder Screen Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun QuizPlaceholderScreenPreview() {
    CodeQuizTheme {
        QuizPlaceholderScreen(
            route = QuizRoute(lessonId = "lesson_1", title = "Introducción a Kotlin"),
            onExit = {}
        )
    }
}
