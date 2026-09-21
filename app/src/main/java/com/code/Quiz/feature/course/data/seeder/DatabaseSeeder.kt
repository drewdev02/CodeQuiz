package com.code.Quiz.feature.course.data.seeder

import android.content.Context
import androidx.room.withTransaction
import com.code.Quiz.core.database.CodeQuizDatabase
import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.dto.SeedDataDto
import com.code.Quiz.feature.course.data.entity.CourseEntity
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import com.code.Quiz.feature.library.data.dao.ArticleDao
import com.code.Quiz.feature.library.data.entity.ArticleEntity
import com.code.Quiz.feature.quiz.data.dao.ExerciseDao
import com.code.Quiz.feature.quiz.data.entity.ExerciseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Seeder responsible for populating the local Room database with initial
 * courses, levels, exercises, and library articles data from the bundled seed JSON asset.
 */
open class DatabaseSeeder(
    private val context: Context? = null,
    private val database: CodeQuizDatabase? = null,
    private val courseDao: CourseDao? = null,
    private val courseLevelDao: CourseLevelDao? = null,
    private val exerciseDao: ExerciseDao? = null,
    private val articleDao: ArticleDao? = null,
    private val dispatcherProvider: DispatcherProvider? = null
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Seeds initial courses, course levels, exercises, and library articles if the database is currently empty.
     * Returns true if database was seeded, false if already populated.
     */
    open suspend fun seedIfNeeded(): Boolean {
        val dispatcher = dispatcherProvider?.io ?: Dispatchers.IO
        return withContext(dispatcher) {
            val cDao = courseDao ?: return@withContext false
            val clDao = courseLevelDao ?: return@withContext false
            val db = database ?: return@withContext false
            val ctx = context ?: return@withContext false

            val shouldSeedCourses = cDao.count() == 0
            val shouldSeedExercises = exerciseDao != null && exerciseDao.count() == 0
            val shouldSeedArticles = articleDao != null && articleDao.count() == 0

            if (!shouldSeedCourses && !shouldSeedExercises && !shouldSeedArticles) {
                return@withContext false
            }

            val jsonString = ctx.assets.open("data/seed_data.json").bufferedReader().use {
                it.readText()
            }
            val seedData = json.decodeFromString<SeedDataDto>(jsonString)

            val courses = seedData.courses.map { courseDto ->
                CourseEntity(
                    id = courseDto.id,
                    title = courseDto.title,
                    description = courseDto.description,
                    orderIndex = courseDto.order,
                    badge = courseDto.badge,
                    iconType = courseDto.iconType,
                    isUnlocked = courseDto.isUnlocked
                )
            }

            val levels = seedData.courses.flatMap { courseDto ->
                courseDto.levels.map { levelDto ->
                    CourseLevelEntity(
                        id = levelDto.id,
                        courseId = courseDto.id,
                        levelNumber = levelDto.levelNumber,
                        title = levelDto.title,
                        description = levelDto.description,
                        status = levelDto.status,
                        totalExercises = levelDto.totalExercises,
                        xpReward = levelDto.xpReward
                    )
                }
            }

            val exercises = seedData.courses.flatMap { courseDto ->
                courseDto.levels.flatMap { levelDto ->
                    levelDto.exercises.map { exerciseDto ->
                        ExerciseEntity(
                            id = exerciseDto.id,
                            levelId = levelDto.id,
                            question = exerciseDto.question,
                            codeSnippet = exerciseDto.codeSnippet,
                            language = exerciseDto.language,
                            optionsJson = json.encodeToString(exerciseDto.options),
                            explanation = exerciseDto.explanation
                        )
                    }
                }
            }

            val articles = seedData.library.map { articleDto ->
                ArticleEntity(
                    id = articleDto.id,
                    title = articleDto.title,
                    category = articleDto.category,
                    summary = articleDto.summary,
                    readTimeMinutes = articleDto.readTimeMinutes,
                    linkedCourseId = articleDto.linkedCourseId,
                    linkedLevelId = articleDto.linkedLevelId,
                    blocksJson = json.encodeToString(articleDto.blocks)
                )
            }

            db.withTransaction {
                if (shouldSeedCourses) {
                    cDao.insertCourses(courses)
                    clDao.insertLevels(levels)
                }
                if (shouldSeedExercises && exercises.isNotEmpty()) {
                    exerciseDao?.insertExercises(exercises)
                }
                if (shouldSeedArticles && articles.isNotEmpty()) {
                    articleDao?.insertArticles(articles)
                }
            }

            true
        }
    }
}
