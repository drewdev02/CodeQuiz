package com.code.Quiz.feature.course.ui

import com.code.Quiz.feature.course.domain.model.Course

/**
 * UI State hierarchy for the Course Path Roadmap screen following UDF.
 */
sealed interface CourseUiState {
    data object Loading : CourseUiState
    data class Success(val courses: List<Course>) : CourseUiState
    data class Error(val message: String) : CourseUiState
}
