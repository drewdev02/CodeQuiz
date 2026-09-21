package com.code.Quiz.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object HomeRoute

@Serializable
data object CoursesRoute

@Serializable
data object ProgressRoute

@Serializable
data object LibraryRoute

@Serializable
data class ArticleDetailRoute(
    val articleId: String
)

@Serializable
data class CourseLevelsRoute(
    val courseId: String,
    val courseTitle: String
)

@Serializable
data class QuizRoute(
    val lessonId: String,
    val title: String = ""
)

