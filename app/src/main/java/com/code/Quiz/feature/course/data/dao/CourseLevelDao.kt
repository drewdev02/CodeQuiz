package com.code.Quiz.feature.course.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for accessing and mutating course levels in Room.
 */
@Dao
interface CourseLevelDao {
    @Query("SELECT * FROM course_levels WHERE courseId = :courseId ORDER BY levelNumber ASC")
    fun getLevelsForCourse(courseId: String): Flow<List<CourseLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<CourseLevelEntity>)

    @Query("UPDATE course_levels SET status = :status WHERE id = :levelId")
    suspend fun updateLevelStatus(levelId: String, status: String)

    @Query("SELECT COUNT(*) FROM course_levels")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM course_levels WHERE status = 'COMPLETED'")
    fun getCompletedLevelsCount(): Flow<Int>

    @Query("SELECT * FROM course_levels WHERE id = :levelId LIMIT 1")
    suspend fun getLevelById(levelId: String): CourseLevelEntity?

    @Query("SELECT * FROM course_levels WHERE courseId = :courseId ORDER BY levelNumber ASC")
    suspend fun getLevelsByCourse(courseId: String): List<CourseLevelEntity>

    @Query("SELECT * FROM course_levels")
    suspend fun getAllLevels(): List<CourseLevelEntity>
}
