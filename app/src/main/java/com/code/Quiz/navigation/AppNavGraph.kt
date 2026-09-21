package com.code.Quiz.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.code.Quiz.core.ui.component.navigation.BottomBarTab
import com.code.Quiz.core.ui.component.navigation.CodeQuizBottomBar
import com.code.Quiz.feature.course.ui.CourseLevelsScreen
import com.code.Quiz.feature.course.ui.CoursePathScreen
import com.code.Quiz.feature.library.ui.ArticleDetailScreen
import com.code.Quiz.feature.library.ui.LibraryScreen
import com.code.Quiz.feature.onboarding.ui.OnboardingScreen
import com.code.Quiz.feature.profile.ui.ProgressScreen
import com.code.Quiz.feature.quiz.ui.QuizScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appNavViewModel: AppNavViewModel = koinViewModel()
) {
    val isOnboardingCompleted by appNavViewModel.isOnboardingCompleted.collectAsState()
    val startDestination: Any = if (isOnboardingCompleted == true) CoursesRoute else OnboardingRoute

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isCoursesRoute = currentDestination?.hasRoute(CoursesRoute::class) == true
    val isLibraryRoute = currentDestination?.hasRoute(LibraryRoute::class) == true
    val isProgressRoute = currentDestination?.hasRoute(ProgressRoute::class) == true
    val showBottomBar = isCoursesRoute || isLibraryRoute || isProgressRoute

    val currentTab = when {
        isProgressRoute -> BottomBarTab.PROGRESS
        isLibraryRoute -> BottomBarTab.LIBRARY
        else -> BottomBarTab.COURSES
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                CodeQuizBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { selectedTab ->
                        val targetRoute: Any = when (selectedTab) {
                            BottomBarTab.COURSES -> CoursesRoute
                            BottomBarTab.LIBRARY -> LibraryRoute
                            BottomBarTab.PROGRESS -> ProgressRoute
                        }
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        key(startDestination) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable<OnboardingRoute> {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            appNavViewModel.completeOnboarding()
                            navController.navigate(CoursesRoute) {
                                popUpTo<OnboardingRoute> {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable<CoursesRoute> {
                    CoursePathScreen(
                        onCourseSelected = { courseId, title ->
                            navController.navigate(
                                CourseLevelsRoute(
                                    courseId = courseId,
                                    courseTitle = title
                                )
                            )
                        }
                    )
                }

                composable<LibraryRoute> {
                    LibraryScreen(
                        onArticleClick = { articleId ->
                            navController.navigate(ArticleDetailRoute(articleId = articleId))
                        }
                    )
                }

                composable<ArticleDetailRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<ArticleDetailRoute>()
                    ArticleDetailScreen(
                        articleId = route.articleId,
                        onBack = { navController.popBackStack() },
                        onNavigateToLevel = { levelId, title ->
                            navController.navigate(QuizRoute(lessonId = levelId, title = title))
                        }
                    )
                }

                composable<ProgressRoute> {
                    ProgressScreen()
                }

                composable<HomeRoute> {
                    CoursePathScreen(
                        onCourseSelected = { courseId, title ->
                            navController.navigate(
                                CourseLevelsRoute(
                                    courseId = courseId,
                                    courseTitle = title
                                )
                            )
                        }
                    )
                }

                composable<CourseLevelsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<CourseLevelsRoute>()
                    CourseLevelsScreen(
                        courseId = route.courseId,
                        courseTitle = route.courseTitle,
                        onBack = { navController.popBackStack() },
                        onStartLevel = { levelId, title ->
                            navController.navigate(QuizRoute(lessonId = levelId, title = title))
                        }
                    )
                }

                composable<QuizRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<QuizRoute>()
                    QuizScreen(
                        levelId = route.lessonId,
                        levelTitle = route.title,
                        onExit = { navController.popBackStack() }
                    )
                }
            }
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

@Preview(
    name = "Quiz Placeholder Screen Preview",
    showBackground = true,
    backgroundColor = 0xFF020813
)
@Composable
private fun QuizPlaceholderScreenPreview() {
    CodeQuizTheme {
        QuizPlaceholderScreen(
            route = QuizRoute(lessonId = "lesson_1", title = "Introducción a Kotlin"),
            onExit = {}
        )
    }
}
