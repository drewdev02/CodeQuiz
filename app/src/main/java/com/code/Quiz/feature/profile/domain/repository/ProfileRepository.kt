package com.code.Quiz.feature.profile.domain.repository

import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for user profile and progress statistics.
 */
interface ProfileRepository {
    fun getUserStats(): Flow<UserProgressStats>
}
