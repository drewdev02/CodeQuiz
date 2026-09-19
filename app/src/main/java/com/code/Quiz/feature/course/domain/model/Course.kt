package com.code.Quiz.feature.course.domain.model

/**
 * Domain representation of a learning roadmap course.
 * Pure Kotlin domain model with zero Android framework dependencies.
 */
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val badge: String? = null,
    val iconType: CourseIconType = CourseIconType.ROCKET,
    val isUnlocked: Boolean = true
)

enum class CourseIconType {
    ROCKET,
    CODE,
    PYTHON,
    DATABASE,
    TERMINAL
}
