package com.code.Quiz.feature.library.data.entity

import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleMapperTest {

    @Test
    fun toDomain_mapsAllFieldsAndBlocksCorrectly() {
        val blocksJson = """
            [
                {"type":"TEXT","content":"Explicación detallada","language":""},
                {"type":"CODE","content":"val x = 10","language":"Kotlin"},
                {"type":"NOTE","content":"Consejo pedagógico","language":""}
            ]
        """.trimIndent()

        val entity = ArticleEntity(
            id = "art_test",
            title = "Artículo de Prueba",
            category = "Fundamentos",
            summary = "Resumen del artículo",
            readTimeMinutes = 5,
            linkedCourseId = "course_1",
            linkedLevelId = "level_1",
            blocksJson = blocksJson
        )

        val domain = entity.toDomain()

        assertEquals("art_test", domain.id)
        assertEquals("Artículo de Prueba", domain.title)
        assertEquals("Fundamentos", domain.category)
        assertEquals("Resumen del artículo", domain.summary)
        assertEquals(5, domain.readTimeMinutes)
        assertEquals("course_1", domain.linkedCourseId)
        assertEquals("level_1", domain.linkedLevelId)
        assertEquals(3, domain.blocks.size)

        assertTrue(domain.blocks[0] is ArticleBlock.Text)
        assertEquals("Explicación detallada", (domain.blocks[0] as ArticleBlock.Text).content)

        assertTrue(domain.blocks[1] is ArticleBlock.Code)
        val codeBlock = domain.blocks[1] as ArticleBlock.Code
        assertEquals("val x = 10", codeBlock.content)
        assertEquals("Kotlin", codeBlock.language)

        assertTrue(domain.blocks[2] is ArticleBlock.Note)
        assertEquals("Consejo pedagógico", (domain.blocks[2] as ArticleBlock.Note).content)
    }

    @Test
    fun toDomain_handlesInvalidOrCorruptedJsonGracefully() {
        val entity = ArticleEntity(
            id = "art_broken",
            title = "Artículo con JSON roto",
            category = "Lógica",
            summary = "Resumen",
            readTimeMinutes = 2,
            linkedCourseId = null,
            linkedLevelId = null,
            blocksJson = "invalid-json-content"
        )

        val domain = entity.toDomain()

        assertEquals("art_broken", domain.id)
        assertTrue(domain.blocks.isEmpty())
    }

    @Test
    fun toEntity_roundTripFidelity() {
        val domain = Article(
            id = "art_roundtrip",
            title = "Roundtrip Test",
            category = "Arquitectura",
            summary = "Verificando roundtrip",
            readTimeMinutes = 4,
            linkedCourseId = "intro_programming",
            linkedLevelId = "intro_programming_level_5",
            blocks = listOf(
                ArticleBlock.Text("Texto"),
                ArticleBlock.Code("fun test() {}", "Kotlin"),
                ArticleBlock.Note("Nota")
            )
        )

        val entity = domain.toEntity()
        val mappedBack = entity.toDomain()

        assertEquals(domain.id, mappedBack.id)
        assertEquals(domain.title, mappedBack.title)
        assertEquals(domain.category, mappedBack.category)
        assertEquals(domain.summary, mappedBack.summary)
        assertEquals(domain.readTimeMinutes, mappedBack.readTimeMinutes)
        assertEquals(domain.linkedCourseId, mappedBack.linkedCourseId)
        assertEquals(domain.linkedLevelId, mappedBack.linkedLevelId)
        assertEquals(domain.blocks.size, mappedBack.blocks.size)
        assertEquals(domain.blocks, mappedBack.blocks)
    }
}
