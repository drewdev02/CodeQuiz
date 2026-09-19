package com.code.Quiz.feature.course.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.Quiz.feature.course.data.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for accessing and mutating courses in Room.
 */
@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY orderIndex ASC")
    fun getCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: String): CourseEntity?

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun count(): Int
}
