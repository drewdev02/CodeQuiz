package com.code.Quiz.feature.library.ui

import com.code.Quiz.feature.library.domain.model.Article

/**
 * UI State for the Library screen following Unidirectional Data Flow (UDF).
 */
sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(
        val categories: List<String>,
        val selectedCategory: String,
        val articles: List<Article>
    ) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
