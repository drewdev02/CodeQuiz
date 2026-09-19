package com.code.Quiz.feature.course.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus

/**
 * Room entity representing a course level in the local database.
 */
@Entity(
    tableName = "course_levels",
    indices = [Index(value = ["courseId"])]
)
data class CourseLevelEntity(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val levelNumber: Int,
    val title: String,
    val description: String,
    val status: String,
    val totalExercises: Int,
    val xpReward: Int
)

/**
 * Maps [CourseLevelEntity] to domain model [CourseLevel].
 */
fun CourseLevelEntity.toDomain(): CourseLevel = CourseLevel(
    id = id,
    courseId = courseId,
    levelNumber = levelNumber,
    title = title,
    description = description,
    status = try {
        LevelStatus.valueOf(status)
    } catch (_: IllegalArgumentException) {
        LevelStatus.LOCKED
    },
    totalExercises = totalExercises,
    xpReward = xpReward
)

/**
 * Maps domain model [CourseLevel] to Room [CourseLevelEntity].
 */
fun CourseLevel.toEntity(): CourseLevelEntity = CourseLevelEntity(
    id = id,
    courseId = courseId,
    levelNumber = levelNumber,
    title = title,
    description = description,
    status = status.name,
    totalExercises = totalExercises,
    xpReward = xpReward
)
