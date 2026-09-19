package com.code.Quiz.feature.course.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the Gamified Course Levels Map screen.
 * Handles level data retrieval, bottom sheet level selection,
 * and maintains immutable UI state following UDF.
 */
class CourseLevelsViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseLevelsUiState>(CourseLevelsUiState.Loading)
    val uiState: StateFlow<CourseLevelsUiState> = _uiState.asStateFlow()

    private var currentCourseId: String? = null

    fun loadLevels(courseId: String, courseTitle: String) {
        if (currentCourseId == courseId && _uiState.value is CourseLevelsUiState.Success) {
            return
        }
        currentCourseId = courseId

        viewModelScope.launch {
            _uiState.value = CourseLevelsUiState.Loading
            courseRepository.getCourseLevels(courseId)
                .catch { throwable ->
                    _uiState.value = CourseLevelsUiState.Error(
                        throwable.message ?: "Error al cargar los niveles del curso"
                    )
                }
                .collect { levels ->
                    _uiState.value = CourseLevelsUiState.Success(
                        courseTitle = courseTitle,
                        levels = levels,
                        selectedLevel = null
                    )
                }
        }
    }

    fun onSelectLevel(level: CourseLevel?) {
        val current = _uiState.value
        if (current is CourseLevelsUiState.Success) {
            _uiState.value = current.copy(selectedLevel = level)
        }
    }
}
