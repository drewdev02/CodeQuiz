package com.code.Quiz.feature.library.domain.model

/**
 * Represents an individual content block within an educational article.
 * Models text explanations, code snippets with optional syntax highlighting language,
 * and pedagogical callout notes.
 */
sealed interface ArticleBlock {
    data class Text(val content: String) : ArticleBlock
    data class Code(val content: String, val language: String = "") : ArticleBlock
    data class Note(val content: String) : ArticleBlock
}
