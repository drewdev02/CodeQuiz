package com.code.Quiz.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.CourseEntity
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import com.code.Quiz.feature.library.data.dao.ArticleDao
import com.code.Quiz.feature.library.data.entity.ArticleEntity
import com.code.Quiz.feature.quiz.data.dao.ExerciseDao
import com.code.Quiz.feature.quiz.data.entity.ExerciseEntity

/**
 * Main Room database for CodeQuiz.
 * Hosts course roadmap, levels, exercises for micro-challenges, and library articles.
 */
@Database(
    entities = [
        CourseEntity::class,
        CourseLevelEntity::class,
        ExerciseEntity::class,
        ArticleEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CodeQuizDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseLevelDao(): CourseLevelDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun articleDao(): ArticleDao
}
