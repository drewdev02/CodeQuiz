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
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeLibraryRepository
    private lateinit var viewModel: LibraryViewModel

    private val sampleArticles = listOf(
        Article(
            id = "art_1",
            title = "Variables",
            category = "Fundamentos",
            summary = "Resumen 1",
            readTimeMinutes = 3,
            blocks = listOf(ArticleBlock.Text("Texto 1"))
        ),
        Article(
            id = "art_2",
            title = "Operadores",
            category = "Fundamentos",
            summary = "Resumen 2",
            readTimeMinutes = 4,
            blocks = listOf(ArticleBlock.Text("Texto 2"))
        ),
        Article(
            id = "art_3",
            title = "Condicionales",
            category = "Lógica",
            summary = "Resumen 3",
            readTimeMinutes = 3,
            blocks = listOf(ArticleBlock.Text("Texto 3"))
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeLibraryRepository(sampleArticles)
        viewModel = LibraryViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyLoadsAndEmitsSuccessWithDerivedCategories() = runTest(testDispatcher) {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is LibraryUiState.Success)

        val success = state as LibraryUiState.Success
        assertEquals(listOf("Todos", "Fundamentos", "Lógica"), success.categories)
        assertEquals("Todos", success.selectedCategory)
        assertEquals(3, success.articles.size)
    }

    @Test
    fun selectCategory_filtersArticlesCorrectly() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.selectCategory("Lógica")
        advanceUntilIdle()

        val state = viewModel.uiState.value as LibraryUiState.Success
        assertEquals("Lógica", state.selectedCategory)
        assertEquals(1, state.articles.size)
        assertEquals("art_3", state.articles[0].id)

        // Switch back to "Todos"
        viewModel.selectCategory("Todos")
        advanceUntilIdle()

        val allState = viewModel.uiState.value as LibraryUiState.Success
        assertEquals("Todos", allState.selectedCategory)
        assertEquals(3, allState.articles.size)
    }

    @Test
    fun uiState_onRepositoryError_emitsErrorState() = runTest(testDispatcher) {
        fakeRepository.shouldThrow = true
        val errorVm = LibraryViewModel(fakeRepository)

        advanceUntilIdle()

        val state = errorVm.uiState.value
        assertTrue("State should be Error", state is LibraryUiState.Error)
        assertEquals("Error de prueba", (state as LibraryUiState.Error).message)
    }

    private class FakeLibraryRepository(
        articles: List<Article>
    ) : LibraryRepository {
        var shouldThrow = false
        private val articlesFlow = MutableStateFlow(articles)

        override fun getArticles(): Flow<List<Article>> = flow {
            if (shouldThrow) {
                throw RuntimeException("Error de prueba")
            }
            articlesFlow.collect { emit(it) }
        }

        override fun getArticleById(id: String): Flow<Article?> = flow {
            if (shouldThrow) {
                throw RuntimeException("Error de prueba")
            }
            emit(articlesFlow.value.find { it.id == id })
        }
    }
}
