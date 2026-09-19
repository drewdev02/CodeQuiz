package com.code.Quiz.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.Quiz.core.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppNavViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean?> =
        userPreferencesRepository.isOnboardingCompleted
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }
}
