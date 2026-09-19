package com.code.Quiz.feature.course.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel managing UI state for the Course Roadmap Path screen.
 */
class CourseViewModel(
    courseRepository: CourseRepository
) : ViewModel() {

    val uiState: StateFlow<CourseUiState> = courseRepository.getCoursePath()
        .map<List<Course>, CourseUiState> { courses ->
            CourseUiState.Success(courses)
        }
        .catch { throwable ->
            emit(CourseUiState.Error(throwable.message ?: "Error al cargar la ruta de cursos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CourseUiState.Loading
        )
}
