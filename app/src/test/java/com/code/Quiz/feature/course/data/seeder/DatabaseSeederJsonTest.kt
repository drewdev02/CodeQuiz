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

        assertEquals(2, dto.version)
        assertEquals(2, dto.courses.size)

        // 1. intro_programming
        val c1 = dto.courses[0]
        assertEquals("intro_programming", c1.id)
        assertEquals(1, c1.order)
        assertEquals("Curso Top", c1.badge)
        assertEquals("ROCKET", c1.iconType)
        assertTrue(c1.isUnlocked)
        assertEquals(5, c1.levels.size)
        assertEquals("ACTIVE", c1.levels[0].status)

        // 2. kotlin_basics
        val c2 = dto.courses[1]
        assertEquals("kotlin_basics", c2.id)
        assertEquals(2, c2.order)
        assertEquals("Recomendado", c2.badge)
        assertEquals("CODE", c2.iconType)
        assertEquals(5, c2.levels.size)

        // 3. library articles verification
        assertEquals(6, dto.library.size)

        val art1 = dto.library[0]
        assertEquals("art_variables_tipos", art1.id)
        assertEquals("Variables y Espacios de Memoria", art1.title)
        assertEquals("Fundamentos", art1.category)
        assertEquals(3, art1.readTimeMinutes)
        assertEquals("intro_programming", art1.linkedCourseId)
        assertEquals("intro_programming_level_1", art1.linkedLevelId)
        assertEquals(3, art1.blocks.size)
        assertEquals("TEXT", art1.blocks[0].type)
        assertEquals("CODE", art1.blocks[1].type)
        assertEquals("NOTE", art1.blocks[2].type)

        val art2 = dto.library[1]
        assertEquals("art_operadores_expresiones", art2.id)
        assertEquals("Operadores y Evaluación Lógica", art2.title)
        assertEquals("Fundamentos", art2.category)
        assertEquals(4, art2.readTimeMinutes)

        val art3 = dto.library[2]
        assertEquals("art_condicionales_flujo", art3.id)
        assertEquals("Control de Flujo: Estructuras Condicionales", art3.title)
        assertEquals("Lógica", art3.category)

        val art4 = dto.library[3]
        assertEquals("art_bucles_repeticion", art4.id)
        assertEquals("Bucles y Ciclos: Automatizando la Repetición", art4.title)
        assertEquals("Lógica", art4.category)

        val art5 = dto.library[4]
        assertEquals("art_null_safety", art5.id)
        assertEquals("Null Safety y Seguridad en Tipos", art5.title)
        assertEquals("Buenas Prácticas", art5.category)
        assertEquals("kotlin_basics", art5.linkedCourseId)
        assertEquals("kotlin_basics_level_1", art5.linkedLevelId)

        val art6 = dto.library[5]
        assertEquals("art_funciones_modularidad", art6.id)
        assertEquals("Funciones Limpias y Modularidad", art6.title)
        assertEquals("Arquitectura", art6.category)
        assertEquals("intro_programming", art6.linkedCourseId)
        assertEquals("intro_programming_level_5", art6.linkedLevelId)
    }
}
