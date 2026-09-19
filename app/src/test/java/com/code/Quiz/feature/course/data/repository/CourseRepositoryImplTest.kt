package com.code.Quiz.feature.course.data.repository

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseDao
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.entity.CourseEntity
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.course.domain.model.CourseIconType
import com.code.Quiz.feature.course.domain.model.LevelStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CourseRepositoryImplTest {

    private lateinit var fakeCourseDao: FakeCourseDao
    private lateinit var fakeCourseLevelDao: FakeCourseLevelDao
    private lateinit var fakeDatabaseSeeder: FakeDatabaseSeeder
    private lateinit var repository: CourseRepositoryImpl

    @Before
    fun setUp() {
        fakeCourseDao = FakeCourseDao()
        fakeCourseLevelDao = FakeCourseLevelDao()
        fakeDatabaseSeeder = FakeDatabaseSeeder(fakeCourseDao, fakeCourseLevelDao)
        repository = CourseRepositoryImpl(
            courseDao = fakeCourseDao,
            courseLevelDao = fakeCourseLevelDao,
            databaseSeeder = fakeDatabaseSeeder,
            dispatcherProvider = DefaultDispatcherProvider()
        )
    }

    @Test
    fun getCoursePath_emitsFourOrderedCourses() = runBlocking {
        val courses = repository.getCoursePath().first()

        assertEquals(1, fakeDatabaseSeeder.seedCallCount)
        assertEquals(4, courses.size)

        // 1. Intro programming
        val course1 = courses[0]
        assertEquals("intro_programming", course1.id)
        assertEquals(1, course1.order)
        assertEquals("Curso Top", course1.badge)
        assertEquals(CourseIconType.ROCKET, course1.iconType)
        assertTrue(course1.isUnlocked)

        // 2. Kotlin basics
        val course2 = courses[1]
        assertEquals("kotlin_basics", course2.id)
        assertEquals(2, course2.order)
        assertEquals("Recomendado", course2.badge)
        assertEquals(CourseIconType.CODE, course2.iconType)
        assertTrue(course2.isUnlocked)

        // 3. Data structures
        val course3 = courses[2]
        assertEquals("data_structures", course3.id)
        assertEquals(3, course3.order)
        assertNull(course3.badge)
        assertEquals(CourseIconType.DATABASE, course3.iconType)
        assertTrue(course3.isUnlocked)

        // 4. Algorithms
        val course4 = courses[3]
        assertEquals("algorithms_logic", course4.id)
        assertEquals(4, course4.order)
        assertEquals("Avanzado", course4.badge)
        assertEquals(CourseIconType.TERMINAL, course4.iconType)
        assertTrue(course4.isUnlocked)
    }

    @Test
    fun getCourseLevels_introProgramming_emitsSevenRealisticLevels() = runBlocking {
        val levels = repository.getCourseLevels("intro_programming").first()

        assertEquals(1, fakeDatabaseSeeder.seedCallCount)
        assertEquals(7, levels.size)

        // Level 1: Completed
        assertEquals("intro_prog_level_1", levels[0].id)
        assertEquals(1, levels[0].levelNumber)
        assertEquals("Variables y Tipos de Datos", levels[0].title)
        assertEquals(LevelStatus.COMPLETED, levels[0].status)
        assertEquals(5, levels[0].totalExercises)
        assertEquals(30, levels[0].xpReward)

        // Level 2: Completed
        assertEquals("intro_prog_level_2", levels[1].id)
        assertEquals(2, levels[1].levelNumber)
        assertEquals("Operadores y Expresiones", levels[1].title)
        assertEquals(LevelStatus.COMPLETED, levels[1].status)

        // Level 3: Active
        assertEquals("intro_prog_level_3", levels[2].id)
        assertEquals(3, levels[2].levelNumber)
        assertEquals("Estructuras Condicionales", levels[2].title)
        assertEquals(LevelStatus.ACTIVE, levels[2].status)

        // Level 4: Locked
        assertEquals("intro_prog_level_4", levels[3].id)
        assertEquals(4, levels[3].levelNumber)
        assertEquals("Bucles y Ciclos", levels[3].title)
        assertEquals(LevelStatus.LOCKED, levels[3].status)

        // Level 7: Locked final exam
        assertEquals("intro_prog_level_7", levels[6].id)
        assertEquals(7, levels[6].levelNumber)
        assertEquals("Algoritmos y Reto Final", levels[6].title)
        assertEquals(LevelStatus.LOCKED, levels[6].status)
        assertEquals(10, levels[6].totalExercises)
        assertEquals(100, levels[6].xpReward)
    }

    @Test
    fun getCourseLevels_defaultCourse_emitsLevels() = runBlocking {
        val levels = repository.getCourseLevels("custom_course").first()

        assertTrue(levels.isNotEmpty())
        assertEquals("custom_course", levels[0].courseId)
    }

    class FakeCourseDao : CourseDao {
        private val coursesFlow = MutableStateFlow<List<CourseEntity>>(emptyList())

        override fun getCourses(): Flow<List<CourseEntity>> = coursesFlow

        override suspend fun insertCourses(courses: List<CourseEntity>) {
            coursesFlow.value = courses
        }

        override suspend fun getCourseById(id: String): CourseEntity? =
            coursesFlow.value.find { it.id == id }

        override suspend fun count(): Int = coursesFlow.value.size
    }

    class FakeCourseLevelDao : CourseLevelDao {
        private val levelsFlow = MutableStateFlow<List<CourseLevelEntity>>(emptyList())

        override fun getLevelsForCourse(courseId: String): Flow<List<CourseLevelEntity>> =
            levelsFlow.map { list -> list.filter { it.courseId == courseId } }

        override suspend fun insertLevels(levels: List<CourseLevelEntity>) {
            levelsFlow.value = levels
        }

        override suspend fun updateLevelStatus(levelId: String, status: String) {
            levelsFlow.value = levelsFlow.value.map {
                if (it.id == levelId) it.copy(status = status) else it
            }
        }

        override suspend fun count(): Int = levelsFlow.value.size

        override suspend fun getLevelById(levelId: String): CourseLevelEntity? =
            levelsFlow.value.find { it.id == levelId }

        override suspend fun getLevelsByCourse(courseId: String): List<CourseLevelEntity> =
            levelsFlow.value.filter { it.courseId == courseId }.sortedBy { it.levelNumber }
    }

    class FakeDatabaseSeeder(
        private val courseDao: FakeCourseDao,
        private val courseLevelDao: FakeCourseLevelDao
    ) : DatabaseSeeder() {
        var seedCallCount = 0

        override suspend fun seedIfNeeded(): Boolean {
            seedCallCount++
            if (courseDao.count() > 0) return false

            courseDao.insertCourses(
                listOf(
                    CourseEntity(
                        id = "intro_programming",
                        title = "Curso de introducción a la programación",
                        description = "Aprende los fundamentos de la computación, pensamiento lógico, variables y estructuras de control.",
                        orderIndex = 1,
                        badge = "Curso Top",
                        iconType = "ROCKET",
                        isUnlocked = true
                    ),
                    CourseEntity(
                        id = "kotlin_basics",
                        title = "Fundamentos de Kotlin",
                        description = "Sintaxis moderna, inmutabilidad, funciones, clases selladas y null safety sin rodeos.",
                        orderIndex = 2,
                        badge = "Recomendado",
                        iconType = "CODE",
                        isUnlocked = true
                    ),
                    CourseEntity(
                        id = "data_structures",
                        title = "Estructuras de Datos Básicas",
                        description = "Listas, pilas, colas, mapas y árboles explicados con retos visuales e interactivos.",
                        orderIndex = 3,
                        badge = null,
                        iconType = "DATABASE",
                        isUnlocked = true
                    ),
                    CourseEntity(
                        id = "algorithms_logic",
                        title = "Algoritmos y Resolución de Problemas",
                        description = "Búsquedas, ordenamientos y análisis de complejidad de forma práctica.",
                        orderIndex = 4,
                        badge = "Avanzado",
                        iconType = "TERMINAL",
                        isUnlocked = true
                    )
                )
            )

            courseLevelDao.insertLevels(
                listOf(
                    CourseLevelEntity(
                        id = "intro_prog_level_1",
                        courseId = "intro_programming",
                        levelNumber = 1,
                        title = "Variables y Tipos de Datos",
                        description = "Aprende qué es una variable y cómo almacenar datos en memoria.",
                        status = "COMPLETED",
                        totalExercises = 5,
                        xpReward = 30
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_2",
                        courseId = "intro_programming",
                        levelNumber = 2,
                        title = "Operadores y Expresiones",
                        description = "Operaciones aritméticas, lógicas y de comparación para transformar datos.",
                        status = "COMPLETED",
                        totalExercises = 5,
                        xpReward = 35
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_3",
                        courseId = "intro_programming",
                        levelNumber = 3,
                        title = "Estructuras Condicionales",
                        description = "Toma decisiones en tu código evaluando expresiones booleanas con if, else y when.",
                        status = "ACTIVE",
                        totalExercises = 6,
                        xpReward = 40
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_4",
                        courseId = "intro_programming",
                        levelNumber = 4,
                        title = "Bucles y Ciclos",
                        description = "Repite instrucciones eficientemente con bucles while y for.",
                        status = "LOCKED",
                        totalExercises = 6,
                        xpReward = 45
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_5",
                        courseId = "intro_programming",
                        levelNumber = 5,
                        title = "Funciones y Modularidad",
                        description = "Divide tu código en bloques reutilizables, parámetros y retornos limpios.",
                        status = "LOCKED",
                        totalExercises = 7,
                        xpReward = 50
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_6",
                        courseId = "intro_programming",
                        levelNumber = 6,
                        title = "Arreglos y Colecciones",
                        description = "Almacena conjuntos de elementos y accede a ellos por índice.",
                        status = "LOCKED",
                        totalExercises = 8,
                        xpReward = 60
                    ),
                    CourseLevelEntity(
                        id = "intro_prog_level_7",
                        courseId = "intro_programming",
                        levelNumber = 7,
                        title = "Algoritmos y Reto Final",
                        description = "Pon a prueba todo lo aprendido en el examen integrador de lógica algorítmica.",
                        status = "LOCKED",
                        totalExercises = 10,
                        xpReward = 100
                    ),
                    CourseLevelEntity(
                        id = "custom_level_1",
                        courseId = "custom_course",
                        levelNumber = 1,
                        title = "Nivel Personalizado",
                        description = "Descripción personalizada.",
                        status = "ACTIVE",
                        totalExercises = 5,
                        xpReward = 30
                    )
                )
            )

            return true
        }
    }
}
