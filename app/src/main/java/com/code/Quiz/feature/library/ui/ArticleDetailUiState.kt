package com.code.Quiz.feature.library.ui

import com.code.Quiz.feature.library.domain.model.Article

/**
 * UI State for the Article Detail Reader screen.
 */
sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState
    data class Success(val article: Article) : ArticleDetailUiState
    data class Error(val message: String) : ArticleDetailUiState
}
