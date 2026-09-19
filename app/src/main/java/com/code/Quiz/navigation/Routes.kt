package com.code.Quiz.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object HomeRoute

@Serializable
data class QuizRoute(
    val lessonId: String,
    val title: String = ""
)
