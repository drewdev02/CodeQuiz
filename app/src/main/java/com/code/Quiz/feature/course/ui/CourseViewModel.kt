package com.code.Quiz.feature.course.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel managing UI state for the Course Roadmap Path screen.
 */
class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CourseUiState> = refreshTrigger
        .flatMapLatest {
            flow {
                emit(CourseUiState.Loading)
                emitAll(
                    courseRepository.getCoursePath().map { courses ->
                        CourseUiState.Success(courses)
                    }
                )
            }.catch { throwable ->
                emit(CourseUiState.Error(throwable.message ?: "Error al cargar la ruta de cursos"))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CourseUiState.Loading
        )

    fun retry() {
        refreshTrigger.value += 1
    }

    fun loadCourses() {
        retry()
    }
}
