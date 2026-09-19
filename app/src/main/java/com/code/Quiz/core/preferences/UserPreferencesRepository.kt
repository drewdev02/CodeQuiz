package com.code.Quiz.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences and local persistent app settings.
 */
interface UserPreferencesRepository {
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean = true)
}
