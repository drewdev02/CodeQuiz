package com.code.Quiz.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.code.Quiz.core.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Implementation of [UserPreferencesRepository] backed by Jetpack Preferences DataStore.
 */
class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : UserPreferencesRepository {

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
        .flowOn(dispatcherProvider.io)

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETED] = completed
            }
        }
    }

    override val lastCatalogVersion: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LAST_CATALOG_VERSION] ?: 0
        }
        .flowOn(dispatcherProvider.io)

    override suspend fun setLastCatalogVersion(version: Int) {
        withContext(dispatcherProvider.io) {
            dataStore.edit { preferences ->
                preferences[LAST_CATALOG_VERSION] = version
            }
        }
    }

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_CATALOG_VERSION = intPreferencesKey("last_catalog_version")
    }
}
