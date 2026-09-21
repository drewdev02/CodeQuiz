package com.code.Quiz.feature.profile.ui

import com.code.Quiz.feature.profile.domain.model.UserProgressStats

/**
 * UI state hierarchy for the Profile / Progress screen.
 */
sealed interface ProgressUiState {
    data object Loading : ProgressUiState
    data class Success(val stats: UserProgressStats) : ProgressUiState
    data class Error(val message: String) : ProgressUiState
}
