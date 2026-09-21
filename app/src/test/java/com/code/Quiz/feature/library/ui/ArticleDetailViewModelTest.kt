package com.code.Quiz.feature.library.ui

import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.model.ArticleBlock
import com.code.Quiz.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeLibraryRepository
    private lateinit var viewModel: ArticleDetailViewModel

    private val sampleArticle = Article(
        id = "art_detail_1",
        title = "Artículo Detalle",
        category = "Fundamentos",
        summary = "Resumen detallado",
        readTimeMinutes = 3,
        linkedCourseId = "course_1",
        linkedLevelId = "level_1",
        blocks = listOf(
            ArticleBlock.Text("Texto principal"),
            ArticleBlock.Code("val z = 42", "Kotlin"),
            ArticleBlock.Note("Consejo")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeLibraryRepository(listOf(sampleArticle))
        viewModel = ArticleDetailViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadArticle_existingId_emitsSuccessWithArticle() = runTest(testDispatcher) {
        viewModel.loadArticle("art_detail_1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is ArticleDetailUiState.Success)

        val success = state as ArticleDetailUiState.Success
        assertEquals("art_detail_1", success.article.id)
        assertEquals(3, success.article.blocks.size)
    }

    @Test
    fun loadArticle_nonExistingId_emitsError() = runTest(testDispatcher) {
        viewModel.loadArticle("unknown_id")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is ArticleDetailUiState.Error)
        assertEquals("Artículo no encontrado", (state as ArticleDetailUiState.Error).message)
    }

    @Test
    fun loadArticle_onException_emitsError() = runTest(testDispatcher) {
        fakeRepository.shouldThrow = true
        viewModel.loadArticle("art_detail_1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is ArticleDetailUiState.Error)
        assertEquals("Fallo de red", (state as ArticleDetailUiState.Error).message)
    }

    private class FakeLibraryRepository(
        articles: List<Article>
    ) : LibraryRepository {
        var shouldThrow = false
        private val articlesFlow = MutableStateFlow(articles)

        override fun getArticles(): Flow<List<Article>> = flow {
            if (shouldThrow) {
                throw RuntimeException("Fallo de red")
            }
            articlesFlow.collect { emit(it) }
        }

        override fun getArticleById(id: String): Flow<Article?> = flow {
            if (shouldThrow) {
                throw RuntimeException("Fallo de red")
            }
            emit(articlesFlow.value.find { it.id == id })
        }
    }
}
