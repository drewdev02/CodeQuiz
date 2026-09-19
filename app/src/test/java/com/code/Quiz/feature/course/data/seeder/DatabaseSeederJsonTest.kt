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
    fun seedDataJson_parsesCorrectlyWithFourCoursesAndExpectedLevels() {
        val file = File("src/main/assets/data/seed_data.json")
        assertTrue("seed_data.json must exist at expected path", file.exists())

        val content = file.readText()
        val dto = json.decodeFromString<SeedDataDto>(content)

        assertEquals(1, dto.version)
        assertEquals(4, dto.courses.size)

        // 1. intro_programming
        val c1 = dto.courses[0]
        assertEquals("intro_programming", c1.id)
        assertEquals(1, c1.order)
        assertEquals("Curso Top", c1.badge)
        assertEquals("ROCKET", c1.iconType)
        assertTrue(c1.isUnlocked)
        assertEquals(7, c1.levels.size)
        assertEquals("COMPLETED", c1.levels[0].status)
        assertEquals("COMPLETED", c1.levels[1].status)
        assertEquals("ACTIVE", c1.levels[2].status)
        assertEquals("LOCKED", c1.levels[3].status)

        // 2. kotlin_basics
        val c2 = dto.courses[1]
        assertEquals("kotlin_basics", c2.id)
        assertEquals(2, c2.order)
        assertEquals("Recomendado", c2.badge)
        assertEquals("CODE", c2.iconType)
        assertEquals(5, c2.levels.size)
        assertEquals("ACTIVE", c2.levels[0].status)
        assertEquals("LOCKED", c2.levels[1].status)

        // 3. data_structures
        val c3 = dto.courses[2]
        assertEquals("data_structures", c3.id)
        assertEquals(3, c3.order)
        assertEquals(null, c3.badge)
        assertEquals("DATABASE", c3.iconType)
        assertEquals(5, c3.levels.size)
        assertEquals("ACTIVE", c3.levels[0].status)
        assertEquals("LOCKED", c3.levels[1].status)

        // 4. algorithms_logic
        val c4 = dto.courses[3]
        assertEquals("algorithms_logic", c4.id)
        assertEquals(4, c4.order)
        assertEquals("Avanzado", c4.badge)
        assertEquals("TERMINAL", c4.iconType)
        assertEquals(5, c4.levels.size)
        assertEquals("ACTIVE", c4.levels[0].status)
        assertEquals("LOCKED", c4.levels[1].status)
    }
}
