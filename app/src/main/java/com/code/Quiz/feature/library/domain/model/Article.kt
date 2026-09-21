package com.code.Quiz.feature.library.domain.model

/**
 * Domain entity representing an educational article in the library.
 * Pure Kotlin without any framework or Android dependencies.
 */
data class Article(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val readTimeMinutes: Int,
    val linkedCourseId: String? = null,
    val linkedLevelId: String? = null,
    val blocks: List<ArticleBlock>
)
