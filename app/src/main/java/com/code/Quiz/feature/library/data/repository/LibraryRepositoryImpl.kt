package com.code.Quiz.feature.library.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.library.data.dao.ArticleDao
import com.code.Quiz.feature.library.data.entity.toDomain
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [LibraryRepository] backed by local Room database
 * and lazy pre-warmed seeding mechanism.
 * Dispatches database and parsing queries to [DispatcherProvider.io].
 */
class LibraryRepositoryImpl(
    private val articleDao: ArticleDao,
    private val databaseSeeder: DatabaseSeeder,
    private val dispatcherProvider: DispatcherProvider
) : LibraryRepository {

    override fun getArticles(): Flow<List<Article>> = flow {
        databaseSeeder.seedIfNeeded()
        emitAll(
            articleDao.getArticles().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.flowOn(dispatcherProvider.io)

    override fun getArticleById(id: String): Flow<Article?> = flow {
        databaseSeeder.seedIfNeeded()
        emitAll(
            articleDao.getArticleById(id).map { entity ->
                entity?.toDomain()
            }
        )
    }.flowOn(dispatcherProvider.io)
}
