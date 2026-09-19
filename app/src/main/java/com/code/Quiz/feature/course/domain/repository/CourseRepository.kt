package com.code.Quiz.feature.course.domain.repository

import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseLevel
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for accessing the learning roadmap course path and levels.
 * Pure Kotlin abstraction enabling clean architecture and testability.
 */
interface CourseRepository {
    fun getCoursePath(): Flow<List<Course>>
    fun getCourseLevels(courseId: String): Flow<List<CourseLevel>>
}

