package com.code.Quiz.feature.course.data.repository

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.feature.course.domain.model.CourseIconType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseRepositoryImplTest {

    private val repository = CourseRepositoryImpl(
        dispatcherProvider = DefaultDispatcherProvider()
    )

    @Test
    fun getCoursePath_emitsFourOrderedCourses() = runBlocking {
        val courses = repository.getCoursePath().first()

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

        assertEquals(7, levels.size)

        // Level 1: Completed
        assertEquals("intro_prog_level_1", levels[0].id)
        assertEquals(1, levels[0].levelNumber)
        assertEquals("Variables y Tipos de Datos", levels[0].title)
        assertEquals(com.code.Quiz.feature.course.domain.model.LevelStatus.COMPLETED, levels[0].status)
        assertEquals(5, levels[0].totalExercises)
        assertEquals(30, levels[0].xpReward)

        // Level 2: Completed
        assertEquals("intro_prog_level_2", levels[1].id)
        assertEquals(2, levels[1].levelNumber)
        assertEquals("Operadores y Expresiones", levels[1].title)
        assertEquals(com.code.Quiz.feature.course.domain.model.LevelStatus.COMPLETED, levels[1].status)

        // Level 3: Active
        assertEquals("intro_prog_level_3", levels[2].id)
        assertEquals(3, levels[2].levelNumber)
        assertEquals("Estructuras Condicionales", levels[2].title)
        assertEquals(com.code.Quiz.feature.course.domain.model.LevelStatus.ACTIVE, levels[2].status)

        // Level 4: Locked
        assertEquals("intro_prog_level_4", levels[3].id)
        assertEquals(4, levels[3].levelNumber)
        assertEquals("Bucles y Ciclos", levels[3].title)
        assertEquals(com.code.Quiz.feature.course.domain.model.LevelStatus.LOCKED, levels[3].status)

        // Level 7: Locked final exam
        assertEquals("intro_prog_level_7", levels[6].id)
        assertEquals(7, levels[6].levelNumber)
        assertEquals("Algoritmos y Reto Final", levels[6].title)
        assertEquals(com.code.Quiz.feature.course.domain.model.LevelStatus.LOCKED, levels[6].status)
        assertEquals(10, levels[6].totalExercises)
        assertEquals(100, levels[6].xpReward)
    }

    @Test
    fun getCourseLevels_defaultCourse_emitsLevels() = runBlocking {
        val levels = repository.getCourseLevels("custom_course").first()

        assertTrue(levels.isNotEmpty())
        assertEquals("custom_course", levels[0].courseId)
    }
}

