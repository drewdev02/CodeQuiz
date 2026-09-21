package com.code.Quiz.feature.library.data.dto

import kotlinx.serialization.Serializable

/**
 * DTO representing an individual educational article within the library seed catalog.
 */
@Serializable
data class ArticleDto(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val readTimeMinutes: Int,
    val linkedCourseId: String? = null,
    val linkedLevelId: String? = null,
    val blocks: List<ArticleBlockDto> = emptyList()
)

/**
 * DTO representing an individual content block of an article.
 */
@Serializable
data class ArticleBlockDto(
    val type: String,
    val content: String,
    val language: String = ""
)
