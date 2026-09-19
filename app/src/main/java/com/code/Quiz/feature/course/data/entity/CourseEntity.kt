package com.code.Quiz.feature.course.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType

/**
 * Room entity representing a course in the local database.
 */
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val badge: String? = null,
    val iconType: String,
    val isUnlocked: Boolean = true
)

/**
 * Maps [CourseEntity] to domain model [Course].
 */
fun CourseEntity.toDomain(): Course = Course(
    id = id,
    title = title,
    description = description,
    order = orderIndex,
    badge = badge,
    iconType = try {
        CourseIconType.valueOf(iconType)
    } catch (_: IllegalArgumentException) {
        CourseIconType.ROCKET
    },
    isUnlocked = isUnlocked
)

/**
 * Maps domain model [Course] to Room [CourseEntity].
 */
fun Course.toEntity(): CourseEntity = CourseEntity(
    id = id,
    title = title,
    description = description,
    orderIndex = order,
    badge = badge,
    iconType = iconType.name,
    isUnlocked = isUnlocked
)
