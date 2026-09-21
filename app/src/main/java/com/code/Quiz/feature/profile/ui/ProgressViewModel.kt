package com.code.Quiz.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import com.code.Quiz.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel managing user learning progress, streak metrics, and achievements.
 */
class ProgressViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = profileRepository.getUserStats()
        .map<UserProgressStats, ProgressUiState> { stats ->
            ProgressUiState.Success(stats)
        }
        .catch { error ->
            emit(ProgressUiState.Error(error.localizedMessage ?: "Error al cargar tu progreso."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProgressUiState.Loading
        )
}
