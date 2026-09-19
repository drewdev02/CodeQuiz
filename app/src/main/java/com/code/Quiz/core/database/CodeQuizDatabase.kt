package com.code.Quiz.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.CourseEntity
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity

/**
 * Main Room database for CodeQuiz.
 * Hosts course roadmap, levels, and future gamification/quiz entities.
 */
@Database(
    entities = [
        CourseEntity::class,
        CourseLevelEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CodeQuizDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseLevelDao(): CourseLevelDao
}
