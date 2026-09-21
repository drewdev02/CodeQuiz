package com.code.Quiz.feature.quiz.data.repository

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import com.code.Quiz.feature.quiz.data.dao.ExerciseDao
import com.code.Quiz.feature.quiz.data.entity.ExerciseEntity
import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuizRepositoryImplTest {

    private lateinit var fakeExerciseDao: FakeExerciseDao
    private lateinit var fakeCourseLevelDao: FakeCourseLevelDao
    private lateinit var repository: QuizRepositoryImpl

    @Before
    fun setUp() {
        fakeExerciseDao = FakeExerciseDao()
        fakeCourseLevelDao = FakeCourseLevelDao()
        repository = QuizRepositoryImpl(
            exerciseDao = fakeExerciseDao,
            courseLevelDao = fakeCourseLevelDao,
            dispatcherProvider = DefaultDispatcherProvider()
        )
    }

    @Test
    fun getExercisesForLevel_whenDaoHasExercises_emitsMappedDomainExercises() = runBlocking {
        val optionsJson = Json.encodeToString(
            listOf(
                mapOf("id" to "o1", "text" to "var", "isCorrect" to "false"),
                mapOf("id" to "o2", "text" to "val", "isCorrect" to "true")
            )
        )
        fakeExerciseDao.insertExercises(
            listOf(
                ExerciseEntity(
                    id = "ex_1",
                    levelId = "lvl_1",
                    question = "¿Cuál es inmutable?",
                    codeSnippet = "val a = 1",
                    language = "Kotlin",
                    optionsJson = optionsJson,
                    explanation = "val es inmutable"
                )
            )
        )

        val exercises = repository.getExercisesForLevel("lvl_1").first()

        assertEquals(1, exercises.size)
        assertEquals("ex_1", exercises[0].id)
        assertEquals("lvl_1", exercises[0].levelId)
        assertEquals("¿Cuál es inmutable?", exercises[0].question)
        assertEquals("val a = 1", exercises[0].codeSnippet)
    }

    @Test
    fun getExercisesForLevel_whenDaoEmpty_emitsRealisticFallbackExercises() = runBlocking {
        val exercises = repository.getExercisesForLevel("unknown_level").first()

        assertTrue(exercises.isNotEmpty())
        assertTrue("Must provide at least 4-5 fallback exercises", exercises.size >= 5)
        assertEquals("unknown_level", exercises[0].levelId)
        assertTrue(exercises[0].options.any { it.isCorrect })
    }

    @Test
    fun completeLevel_updatesCurrentToCompleted_andUnlocksNextLevel() = runBlocking {
        fakeCourseLevelDao.insertLevels(
            listOf(
                CourseLevelEntity(
                    id = "intro_prog_level_1",
                    courseId = "intro_programming",
                    levelNumber = 1,
                    title = "Nivel 1",
                    description = "Desc 1",
                    status = "ACTIVE",
                    totalExercises = 5,
                    xpReward = 30
                ),
                CourseLevelEntity(
                    id = "intro_prog_level_2",
                    courseId = "intro_programming",
                    levelNumber = 2,
                    title = "Nivel 2",
                    description = "Desc 2",
                    status = "LOCKED",
                    totalExercises = 5,
                    xpReward = 35
                )
            )
        )

        repository.completeLevel(
            courseId = "intro_programming",
            levelId = "intro_prog_level_1",
            xpEarned = 30
        )

        val level1 = fakeCourseLevelDao.getLevelById("intro_prog_level_1")
        val level2 = fakeCourseLevelDao.getLevelById("intro_prog_level_2")

        assertNotNull(level1)
        assertEquals("COMPLETED", level1?.status)
        assertNotNull(level2)
        assertEquals("ACTIVE", level2?.status)
    }

    @Test
    fun completeLevel_lastLevel_updatesToCompletedWithoutError() = runBlocking {
        fakeCourseLevelDao.insertLevels(
            listOf(
                CourseLevelEntity(
                    id = "final_level",
                    courseId = "intro_programming",
                    levelNumber = 7,
                    title = "Reto Final",
                    description = "Desc",
                    status = "ACTIVE",
                    totalExercises = 5,
                    xpReward = 100
                )
            )
        )

        repository.completeLevel(
            courseId = "intro_programming",
            levelId = "final_level",
            xpEarned = 100
        )

        val level = fakeCourseLevelDao.getLevelById("final_level")
        assertEquals("COMPLETED", level?.status)
    }

    class FakeExerciseDao : ExerciseDao {
        private val exercisesFlow = MutableStateFlow<List<ExerciseEntity>>(emptyList())

        override fun getExercisesForLevel(levelId: String): Flow<List<ExerciseEntity>> =
            exercisesFlow.map { list -> list.filter { it.levelId == levelId } }

        override suspend fun insertExercises(exercises: List<ExerciseEntity>) {
            exercisesFlow.value = exercisesFlow.value + exercises
        }

        override suspend fun count(): Int = exercisesFlow.value.size

        override suspend fun deleteOrphanedExercises() {
            // no-op in test fake
        }
    }

    class FakeCourseLevelDao : CourseLevelDao {
        private val levelsFlow = MutableStateFlow<List<CourseLevelEntity>>(emptyList())

        override fun getLevelsForCourse(courseId: String): Flow<List<CourseLevelEntity>> =
            levelsFlow.map { list -> list.filter { it.courseId == courseId } }

        override suspend fun insertLevels(levels: List<CourseLevelEntity>) {
            levelsFlow.value = levelsFlow.value + levels
        }

        override suspend fun updateLevelStatus(levelId: String, status: String) {
            levelsFlow.value = levelsFlow.value.map {
                if (it.id == levelId) it.copy(status = status) else it
            }
        }

        override suspend fun count(): Int = levelsFlow.value.size

        override fun getCompletedLevelsCount(): Flow<Int> =
            levelsFlow.map { list -> list.count { it.status == "COMPLETED" } }

        override suspend fun getLevelById(levelId: String): CourseLevelEntity? =
            levelsFlow.value.find { it.id == levelId }

        override suspend fun getLevelsByCourse(courseId: String): List<CourseLevelEntity> =
            levelsFlow.value.filter { it.courseId == courseId }.sortedBy { it.levelNumber }

        override suspend fun getAllLevels(): List<CourseLevelEntity> = levelsFlow.value

        override suspend fun deleteLevelsForOrphanedCourses(validCourseIds: List<String>) {
            levelsFlow.value = levelsFlow.value.filter { it.courseId in validCourseIds }
        }
    }
}
