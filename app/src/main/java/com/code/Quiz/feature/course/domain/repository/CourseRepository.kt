package com.code.Quiz.feature.course.domain.repository

import com.code.Quiz.feature.course.domain.model.Course
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for accessing the learning roadmap course path.
 * Pure Kotlin abstraction enabling clean architecture and testability.
 */
interface CourseRepository {
    fun getCoursePath(): Flow<List<Course>>
}
