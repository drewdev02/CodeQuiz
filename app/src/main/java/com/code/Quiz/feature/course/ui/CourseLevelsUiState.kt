package com.code.Quiz.feature.course.ui

import com.code.Quiz.feature.course.domain.model.CourseLevel

/**
 * Sealed interface modeling the UI state for the Gamified Course Levels Map screen.
 * Follows Unidirectional Data Flow (UDF) standards.
 */
sealed interface CourseLevelsUiState {
    data object Loading : CourseLevelsUiState
    data class Success(
        val courseTitle: String,
        val levels: List<CourseLevel>,
        val selectedLevel: CourseLevel? = null
    ) : CourseLevelsUiState
    data class Error(val message: String) : CourseLevelsUiState
}
