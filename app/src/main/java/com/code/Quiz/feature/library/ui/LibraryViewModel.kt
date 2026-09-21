package com.code.Quiz.feature.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.library.domain.model.Article
import com.code.Quiz.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the educational library screen.
 * Handles category filtering and reactive updates from [LibraryRepository].
 */
class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var allArticles: List<Article> = emptyList()
    private var selectedCategory: String = "Todos"

    init {
        loadArticles()
    }

    fun selectCategory(category: String) {
        selectedCategory = category
        updateSuccessState()
    }

    fun retry() {
        loadArticles()
    }

    private fun loadArticles() {
        _uiState.value = LibraryUiState.Loading
        viewModelScope.launch {
            libraryRepository.getArticles()
                .catch { exception ->
                    _uiState.value = LibraryUiState.Error(
                        exception.message ?: "Error al cargar la biblioteca"
                    )
                }
                .collect { articles ->
                    allArticles = articles
                    updateSuccessState()
                }
        }
    }

    private fun updateSuccessState() {
        val categories = listOf("Todos") + allArticles.map { it.category }.distinct()
        val filteredArticles = if (selectedCategory == "Todos") {
            allArticles
        } else {
            allArticles.filter { it.category == selectedCategory }
        }
        _uiState.value = LibraryUiState.Success(
            categories = categories,
            selectedCategory = selectedCategory,
            articles = filteredArticles
        )
    }
}
