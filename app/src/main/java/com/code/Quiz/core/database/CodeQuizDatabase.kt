package com.code.Quiz.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.CourseEntity
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import com.code.Quiz.feature.quiz.data.dao.ExerciseDao
import com.code.Quiz.feature.quiz.data.entity.ExerciseEntity

/**
 * Main Room database for CodeQuiz.
 * Hosts course roadmap, levels, and exercises for micro-challenges.
 */
@Database(
    entities = [
        CourseEntity::class,
        CourseLevelEntity::class,
        ExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CodeQuizDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseLevelDao(): CourseLevelDao
    abstract fun exerciseDao(): ExerciseDao
}
