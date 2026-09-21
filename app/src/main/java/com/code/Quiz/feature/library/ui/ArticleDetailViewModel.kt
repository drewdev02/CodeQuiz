package com.code.Quiz.feature.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for viewing detailed educational articles with interactive content blocks.
 */
class ArticleDetailViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    private var currentArticleId: String? = null
    private var loadJob: Job? = null

    fun loadArticle(articleId: String) {
        if (currentArticleId == articleId && _uiState.value is ArticleDetailUiState.Success) {
            return
        }
        currentArticleId = articleId
        loadJob?.cancel()
        _uiState.value = ArticleDetailUiState.Loading

        loadJob = viewModelScope.launch {
            libraryRepository.getArticleById(articleId)
                .catch { exception ->
                    _uiState.value = ArticleDetailUiState.Error(
                        exception.message ?: "Error al cargar el artículo"
                    )
                }
                .collect { article ->
                    if (article != null) {
                        _uiState.value = ArticleDetailUiState.Success(article)
                    } else {
                        _uiState.value = ArticleDetailUiState.Error("Artículo no encontrado")
                    }
                }
        }
    }

    fun retry() {
        currentArticleId?.let { loadArticle(it) }
    }
}
