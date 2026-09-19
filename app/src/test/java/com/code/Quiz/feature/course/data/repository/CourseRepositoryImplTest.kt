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
}
