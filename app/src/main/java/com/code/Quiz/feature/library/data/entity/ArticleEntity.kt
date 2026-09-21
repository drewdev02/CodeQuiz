package com.code.Quiz.feature.library.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.code.Quiz.feature.library.data.dto.ArticleBlockDto
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val articleJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

/**
 * Room entity representing an educational article stored in local persistence.
 */
@Entity(
    tableName = "library_articles",
    indices = [Index("category")]
)
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val readTimeMinutes: Int,
    val linkedCourseId: String? = null,
    val linkedLevelId: String? = null,
    val blocksJson: String
)

/**
 * Maps [ArticleEntity] to pure domain [Article].
 */
fun ArticleEntity.toDomain(): Article {
    val blockDtos = try {
        articleJson.decodeFromString<List<ArticleBlockDto>>(blocksJson)
    } catch (_: Exception) {
        emptyList()
    }

    val domainBlocks = blockDtos.map { dto ->
        when (dto.type.uppercase()) {
            "CODE" -> ArticleBlock.Code(content = dto.content, language = dto.language)
            "NOTE" -> ArticleBlock.Note(content = dto.content)
            else -> ArticleBlock.Text(content = dto.content)
        }
    }

    return Article(
        id = id,
        title = title,
        category = category,
        summary = summary,
        readTimeMinutes = readTimeMinutes,
        linkedCourseId = linkedCourseId,
        linkedLevelId = linkedLevelId,
        blocks = domainBlocks
    )
}

/**
 * Maps domain [Article] to Room [ArticleEntity].
 */
fun Article.toEntity(): ArticleEntity {
    val blockDtos = blocks.map { block ->
        when (block) {
            is ArticleBlock.Text -> ArticleBlockDto(type = "TEXT", content = block.content)
            is ArticleBlock.Code -> ArticleBlockDto(type = "CODE", content = block.content, language = block.language)
            is ArticleBlock.Note -> ArticleBlockDto(type = "NOTE", content = block.content)
        }
    }

    return ArticleEntity(
        id = id,
        title = title,
        category = category,
        summary = summary,
        readTimeMinutes = readTimeMinutes,
        linkedCourseId = linkedCourseId,
        linkedLevelId = linkedLevelId,
        blocksJson = articleJson.encodeToString(blockDtos)
    )
}
