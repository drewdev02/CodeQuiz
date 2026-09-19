package com.code.Quiz.feature.course.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.toDomain
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [CourseRepository] backed by local Room database
 * and lazy pre-warmed seeding mechanism.
 * Executes on [DispatcherProvider.io] per clean architecture standards.
 */
class CourseRepositoryImpl(
    private val courseDao: CourseDao,
    private val courseLevelDao: CourseLevelDao,
    private val databaseSeeder: DatabaseSeeder,
    private val dispatcherProvider: DispatcherProvider
) : CourseRepository {

    override fun getCoursePath(): Flow<List<Course>> = flow {
        databaseSeeder.seedIfNeeded()
        emitAll(
            courseDao.getCourses().map { list -> list.map { it.toDomain() } }
        )
    }.flowOn(dispatcherProvider.io)

    override fun getCourseLevels(courseId: String): Flow<List<CourseLevel>> = flow {
        databaseSeeder.seedIfNeeded()
        emitAll(
            courseLevelDao.getLevelsForCourse(courseId).map { list -> list.map { it.toDomain() } }
        )
    }.flowOn(dispatcherProvider.io)
}
