package com.code.Quiz.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.code.Quiz.core.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserPreferencesRepositoryTest {

    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var testDispatcherProvider: DispatcherProvider
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setUp() {
        fakeDataStore = FakeDataStore()
        testDispatcherProvider = object : DispatcherProvider {
            override val main: CoroutineDispatcher = Dispatchers.Unconfined
            override val io: CoroutineDispatcher = Dispatchers.Unconfined
            override val default: CoroutineDispatcher = Dispatchers.Unconfined
            override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
        }
        repository = UserPreferencesRepositoryImpl(
            dataStore = fakeDataStore,
            dispatcherProvider = testDispatcherProvider
        )
    }

    @Test
    fun defaultOnboardingStatus_isFalse() = runBlocking {
        val isCompleted = repository.isOnboardingCompleted.first()
        assertFalse(isCompleted)
    }

    @Test
    fun setOnboardingCompleted_emitsTrue() = runBlocking {
        repository.setOnboardingCompleted(true)
        val isCompleted = repository.isOnboardingCompleted.first()
        assertTrue(isCompleted)
    }

    @Test
    fun setOnboardingCompleted_defaultParam_emitsTrue() = runBlocking {
        val userPreferencesRepository: UserPreferencesRepository = repository
        userPreferencesRepository.setOnboardingCompleted()
        val isCompleted = repository.isOnboardingCompleted.first()
        assertTrue(isCompleted)
    }

    @Test
    fun setOnboardingCompleted_canBeSetToFalse() = runBlocking {
        repository.setOnboardingCompleted(true)
        assertTrue(repository.isOnboardingCompleted.first())

        repository.setOnboardingCompleted(false)
        assertFalse(repository.isOnboardingCompleted.first())
    }

    @Test
    fun ioExceptionHandling_emitsFalseGracefully() = runBlocking {
        fakeDataStore.shouldThrowIoException = true
        val isCompleted = repository.isOnboardingCompleted.first()
        assertFalse(isCompleted)
    }

    @Test
    fun defaultLastCatalogVersion_isZero() = runBlocking {
        val version = repository.lastCatalogVersion.first()
        assertEquals(0, version)
    }

    @Test
    fun setLastCatalogVersion_emitsUpdatedVersion() = runBlocking {
        repository.setLastCatalogVersion(2)
        val version = repository.lastCatalogVersion.first()
        assertEquals(2, version)

        repository.setLastCatalogVersion(5)
        assertEquals(5, repository.lastCatalogVersion.first())
    }

    @Test
    fun ioExceptionHandling_catalogVersion_emitsZeroGracefully() = runBlocking {
        fakeDataStore.shouldThrowIoException = true
        val version = repository.lastCatalogVersion.first()
        assertEquals(0, version)
    }

    @Test
    fun fakeUserPreferencesRepository_validatesDefaultFalseAndEmitsTrueOnCompletion() = runBlocking {
        val fakeRepo = FakeUserPreferencesRepository()
        assertFalse(fakeRepo.isOnboardingCompleted.first())

        fakeRepo.setOnboardingCompleted(true)
        assertTrue(fakeRepo.isOnboardingCompleted.first())
    }

    @Test
    fun fakeUserPreferencesRepository_validatesDefaultZeroAndEmitsNewVersion() = runBlocking {
        val fakeRepo = FakeUserPreferencesRepository()
        assertEquals(0, fakeRepo.lastCatalogVersion.first())

        fakeRepo.setLastCatalogVersion(3)
        assertEquals(3, fakeRepo.lastCatalogVersion.first())
    }

    class FakeDataStore(initialPreferences: Preferences = emptyPreferences()) : DataStore<Preferences> {
        private val stateFlow = MutableStateFlow(initialPreferences)
        var shouldThrowIoException = false

        override val data: Flow<Preferences> = flow {
            if (shouldThrowIoException) {
                throw IOException("Disk error")
            }
            stateFlow.collect { emit(it) }
        }

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(stateFlow.value)
            stateFlow.value = updated
            return updated
        }
    }

    class FakeUserPreferencesRepository(
        initialCompleted: Boolean = false,
        initialCatalogVersion: Int = 0
    ) : UserPreferencesRepository {
        private val _isOnboardingCompleted = MutableStateFlow(initialCompleted)
        override val isOnboardingCompleted: Flow<Boolean> = _isOnboardingCompleted

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            _isOnboardingCompleted.value = completed
        }

        private val _lastCatalogVersion = MutableStateFlow(initialCatalogVersion)
        override val lastCatalogVersion: Flow<Int> = _lastCatalogVersion

        override suspend fun setLastCatalogVersion(version: Int) {
            _lastCatalogVersion.value = version
        }
    }
}
