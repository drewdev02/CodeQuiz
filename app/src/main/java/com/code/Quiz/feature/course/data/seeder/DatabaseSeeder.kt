package com.code.Quiz.feature.course.data.seeder

import android.content.Context
import androidx.room.withTransaction
import com.code.Quiz.core.database.CodeQuizDatabase
import com.code.Quiz.core.preferences.UserPreferencesRepository
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
import com.code.Quiz.feature.streak.data.dao.StreakDao
import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

/**
 * Seeder responsible for populating the local Room database with initial
 * courses, levels, exercises, library articles, and streak data from the bundled seed JSON asset.
 */
open class DatabaseSeeder(
    private val context: Context? = null,
    private val database: CodeQuizDatabase? = null,
    private val courseDao: CourseDao? = null,
    private val courseLevelDao: CourseLevelDao? = null,
    private val exerciseDao: ExerciseDao? = null,
    private val articleDao: ArticleDao? = null,
    private val streakDao: StreakDao? = null,
    private val userPreferencesRepository: UserPreferencesRepository? = null,
    private val dispatcherProvider: DispatcherProvider? = null
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Seeds initial courses, course levels, exercises, and library articles if the database is currently empty,
     * or synchronizes new catalog versions and missing items while preserving user level progress.
     * Returns true if database was seeded or updated, false if already up-to-date.
     */
    open suspend fun seedIfNeeded(): Boolean {
        val dispatcher = dispatcherProvider?.io ?: Dispatchers.IO
        return withContext(dispatcher) {
            val cDao = courseDao ?: return@withContext false
            val clDao = courseLevelDao ?: return@withContext false
            val db = database ?: return@withContext false
            val ctx = context ?: return@withContext false

            val storedVersion = userPreferencesRepository?.lastCatalogVersion?.firstOrNull() ?: 0

            val jsonString = ctx.assets.open("data/seed_data.json").bufferedReader().use {
                it.readText()
            }
            val seedData = json.decodeFromString<SeedDataDto>(jsonString)

            val isDbEmpty = cDao.count() == 0
            val hasNewCatalogVersion = seedData.version > storedVersion
            val hasMissingCourses = cDao.count() < seedData.courses.size
            val hasMissingArticles = articleDao != null && articleDao.count() < seedData.library.size

            if (!isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles) {
                return@withContext false
            }

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

            val existingLevelsMap = clDao.getAllLevels().associateBy { it.id }
            val levels = seedData.courses.flatMap { courseDto ->
                val completedLevelNumbers = courseDto.levels
                    .mapNotNull { levelDto -> existingLevelsMap[levelDto.id] }
                    .filter { it.status == "COMPLETED" }
                    .map { it.levelNumber }
                    .toSet()
                val maxCompleted = completedLevelNumbers.maxOrNull() ?: 0

                courseDto.levels.map { levelDto ->
                    val effectiveStatus = if (completedLevelNumbers.contains(levelDto.levelNumber)) {
                        "COMPLETED"
                    } else if (levelDto.levelNumber == maxCompleted + 1) {
                        "ACTIVE"
                    } else {
                        "LOCKED"
                    }
                    CourseLevelEntity(
                        id = levelDto.id,
                        courseId = courseDto.id,
                        levelNumber = levelDto.levelNumber,
                        title = levelDto.title,
                        description = levelDto.description,
                        status = effectiveStatus,
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
                val validCourseIds = courses.map { it.id }
                cDao.deleteCoursesNotIn(validCourseIds)
                clDao.deleteLevelsForOrphanedCourses(validCourseIds)
                cDao.insertCourses(courses)
                clDao.insertLevels(levels)
                if (exercises.isNotEmpty()) {
                    exerciseDao?.deleteOrphanedExercises()
                    exerciseDao?.insertExercises(exercises)
                }
                if (articles.isNotEmpty()) {
                    val validArticleIds = articles.map { it.id }
                    articleDao?.deleteArticlesNotIn(validArticleIds)
                    articleDao?.insertArticles(articles)
                }
                if (isDbEmpty && streakDao != null && streakDao.getStreak() == null) {
                    val today = LocalDate.now().toString()
                    streakDao.insertOrUpdateStreak(
                        StreakEntity(
                            id = 1,
                            currentStreak = 1,
                            maxStreak = 1,
                            lastActiveDate = today
                        )
                    )
                    streakDao.insertOrUpdateActivity(
                        DailyActivityEntity(
                            date = today,
                            xpEarned = 70,
                            exercisesCompleted = 10
                        )
                    )
                }
            }

            userPreferencesRepository?.setLastCatalogVersion(seedData.version)

            true
        }
    }
}
