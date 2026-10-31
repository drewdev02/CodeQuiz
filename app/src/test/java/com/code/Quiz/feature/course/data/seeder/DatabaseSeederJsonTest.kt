package com.code.Quiz.feature.course.data.seeder

import com.code.Quiz.feature.course.data.dto.SeedDataDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DatabaseSeederJsonTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun seedDataJson_parsesCorrectlyWithCoursesAndLibraryArticles() {
        val file = File("src/main/assets/data/seed_data.json")
        assertTrue("seed_data.json must exist at expected path", file.exists())

        val content = file.readText()
        val dto = json.decodeFromString<SeedDataDto>(content)

        assertEquals(8, dto.version)
        assertEquals(2, dto.courses.size)

        // 1. C++ Linear Programming Course
        val c1 = dto.courses[0]
        assertEquals("cpp_linear_programming", c1.id)
        assertEquals(1, c1.order)
        assertEquals("Fundamentos", c1.badge)
        assertEquals("CODE", c1.iconType)
        assertTrue(c1.isUnlocked)
        assertEquals(33, c1.levels.size)
        assertEquals("ACTIVE", c1.levels[0].status)
        assertEquals("cpp_lin_level_1", c1.levels[0].id)
        assertEquals(5, c1.levels[0].exercises.size)
        assertEquals("C++", c1.levels[0].exercises[0].language)

        // Verify all C++ levels have <= 5 exercises
        c1.levels.forEach { lvl ->
            assertTrue("Level ${lvl.id} has at most 5 exercises", lvl.exercises.size <= 5)
            assertEquals(lvl.exercises.size, lvl.totalExercises)
        }

        // 2. Java OOP Mastery Course
        val c2 = dto.courses[1]
        assertEquals("java_oop_mastery", c2.id)
        assertEquals(2, c2.order)
        assertEquals("POO / OOP", c2.badge)
        assertEquals("ROCKET", c2.iconType)
        assertTrue(c2.isUnlocked)
        assertEquals(6, c2.levels.size)
        assertEquals("ACTIVE", c2.levels[0].status)
        assertEquals("java_oop_level_1", c2.levels[0].id)
        assertEquals(5, c2.levels[0].exercises.size)
        assertEquals("Java", c2.levels[0].exercises[0].language)

        // Validate all 178 exercises
        val allExercises = dto.courses.flatMap { it.levels.flatMap { lvl -> lvl.exercises } }
        assertEquals(178, allExercises.size)
        allExercises.forEach { ex ->
            assertNotNull(ex.id)
            assertTrue(ex.question.isNotBlank())
            assertTrue(ex.explanation.isNotBlank())
            assertTrue("Difficulty must be valid", ex.difficulty in listOf("EASY", "MEDIUM", "HARD"))
            val correctCount = ex.options.count { it.isCorrect }
            assertTrue("Exercise ${ex.id} must have at least 1 correct option", correctCount >= 1)
        }

        // 3. Library articles verification
        assertEquals(13, dto.library.size)

        val art1 = dto.library[0]
        assertEquals("art_cpp_intro_lineal", art1.id)
        assertEquals("Introducción a C++ y Programación Lineal", art1.title)
        assertEquals("C++ Fundamentos", art1.category)
        assertEquals(4, art1.readTimeMinutes)
        assertEquals("cpp_linear_programming", art1.linkedCourseId)
        assertEquals("cpp_lin_level_1", art1.linkedLevelId)
        assertEquals(3, art1.blocks.size)
        assertEquals("TEXT", art1.blocks[0].type)
        assertEquals("CODE", art1.blocks[1].type)
        assertEquals("NOTE", art1.blocks[2].type)

        val artJava = dto.library.first { it.id == "art_java_clases_objetos" }
        assertEquals("El Paradigma POO: Clases, Objetos e Instanciación en Java", artJava.title)
        assertEquals("Java POO", artJava.category)
        assertEquals("java_oop_mastery", artJava.linkedCourseId)
        assertEquals("java_oop_level_1", artJava.linkedLevelId)
    }
}
