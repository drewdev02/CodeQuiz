package com.code.Quiz.feature.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.Quiz.feature.library.data.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for library educational articles.
 */
@Dao
interface ArticleDao {

    @Query("SELECT * FROM library_articles")
    fun getArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM library_articles WHERE id = :id LIMIT 1")
    fun getArticleById(id: String): Flow<ArticleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT COUNT(*) FROM library_articles")
    suspend fun count(): Int
}
