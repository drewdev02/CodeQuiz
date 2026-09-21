package com.code.Quiz.feature.library.data.repository

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.library.data.dao.ArticleDao
import com.code.Quiz.feature.library.data.entity.ArticleEntity
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LibraryRepositoryImplTest {

    private lateinit var fakeArticleDao: FakeArticleDao
    private lateinit var fakeDatabaseSeeder: FakeDatabaseSeeder
    private lateinit var repository: LibraryRepositoryImpl

    @Before
    fun setUp() {
        fakeArticleDao = FakeArticleDao()
        fakeDatabaseSeeder = FakeDatabaseSeeder()
        repository = LibraryRepositoryImpl(
            articleDao = fakeArticleDao,
            databaseSeeder = fakeDatabaseSeeder,
            dispatcherProvider = DefaultDispatcherProvider()
        )
    }

    @Test
    fun getArticles_callsSeedIfNeededAndReturnsMappedArticles() = runBlocking {
        fakeArticleDao.insertArticles(
            listOf(
                ArticleEntity(
                    id = "art_1",
                    title = "Articulo 1",
                    category = "Fundamentos",
                    summary = "Resumen 1",
                    readTimeMinutes = 3,
                    linkedCourseId = "course_1",
                    linkedLevelId = "level_1",
                    blocksJson = """[{"type":"TEXT","content":"Contenido 1"}]"""
                ),
                ArticleEntity(
                    id = "art_2",
                    title = "Articulo 2",
                    category = "Lógica",
                    summary = "Resumen 2",
                    readTimeMinutes = 4,
                    linkedCourseId = null,
                    linkedLevelId = null,
                    blocksJson = """[{"type":"NOTE","content":"Nota 2"}]"""
                )
            )
        )

        val articles = repository.getArticles().first()

        assertEquals(1, fakeDatabaseSeeder.seedCallCount)
        assertEquals(2, articles.size)
        assertEquals("art_1", articles[0].id)
        assertEquals("Articulo 1", articles[0].title)
        assertEquals("Fundamentos", articles[0].category)
        assertEquals("art_2", articles[1].id)
        assertEquals("Lógica", articles[1].category)
    }

    @Test
    fun getArticleById_existingId_returnsMappedArticle() = runBlocking {
        fakeArticleDao.insertArticles(
            listOf(
                ArticleEntity(
                    id = "art_1",
                    title = "Articulo 1",
                    category = "Fundamentos",
                    summary = "Resumen 1",
                    readTimeMinutes = 3,
                    linkedCourseId = "course_1",
                    linkedLevelId = "level_1",
                    blocksJson = """[{"type":"CODE","content":"val a = 1","language":"Kotlin"}]"""
                )
            )
        )

        val article = repository.getArticleById("art_1").first()

        assertEquals(1, fakeDatabaseSeeder.seedCallCount)
        assertNotNull(article)
        assertEquals("art_1", article?.id)
        assertEquals(1, article?.blocks?.size)
        assertEquals("Kotlin", (article?.blocks?.first() as ArticleBlock.Code).language)
    }

    @Test
    fun getArticleById_nonExistingId_returnsNull() = runBlocking {
        val article = repository.getArticleById("non_existent").first()

        assertEquals(1, fakeDatabaseSeeder.seedCallCount)
        assertNull(article)
    }

    private class FakeArticleDao : ArticleDao {
        private val articlesFlow = MutableStateFlow<List<ArticleEntity>>(emptyList())

        override fun getArticles(): Flow<List<ArticleEntity>> = articlesFlow

        override fun getArticleById(id: String): Flow<ArticleEntity?> {
            return articlesFlow.map { list -> list.find { it.id == id } }
        }

        override suspend fun insertArticles(articles: List<ArticleEntity>) {
            articlesFlow.value = articles
        }

        override suspend fun count(): Int = articlesFlow.value.size

        override suspend fun deleteArticlesNotIn(validIds: List<String>) {
            articlesFlow.value = articlesFlow.value.filter { it.id in validIds }
        }
    }

    private class FakeDatabaseSeeder : DatabaseSeeder() {
        var seedCallCount = 0

        override suspend fun seedIfNeeded(): Boolean {
            seedCallCount++
            return true
        }
    }
}
