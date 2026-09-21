package com.code.Quiz.feature.library.domain.repository

import com.code.Quiz.feature.library.domain.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for accessing educational articles in the library.
 */
interface LibraryRepository {
    fun getArticles(): Flow<List<Article>>
    fun getArticleById(id: String): Flow<Article?>
}
