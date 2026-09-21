package com.code.Quiz.feature.profile.ui

import com.code.Quiz.feature.profile.domain.model.UserProgressStats
import com.code.Quiz.feature.profile.domain.repository.ProfileRepository
import com.code.Quiz.feature.streak.domain.model.Streak
import com.code.Quiz.feature.streak.domain.model.WeekDayStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_whenRepositoryEmits_updatesToSuccessState() = runTest {
        val sampleStats = UserProgressStats(
            streak = Streak(currentStreak = 4, maxStreak = 6, lastActiveDate = "2026-09-20", isActiveToday = true),
            totalXp = 320,
            totalExercisesCompleted = 30,
            completedLevelsCount = 3,
            weekStreak = listOf(
                WeekDayStatus("L", 15, "2026-09-15", isCompleted = true, isToday = false, isFuture = false)
            ),
            activeDatesThisMonth = setOf("2026-09-15", "2026-09-20")
        )

        val repository = object : ProfileRepository {
            override fun getUserStats(): Flow<UserProgressStats> = flowOf(sampleStats)
        }

        val viewModel = ProgressViewModel(repository)
        val collectJob = launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is ProgressUiState.Success)
        val success = state as ProgressUiState.Success
        assertEquals(4, success.stats.streak.currentStreak)
        assertEquals(320, success.stats.totalXp)
        assertEquals(30, success.stats.totalExercisesCompleted)
        assertEquals(3, success.stats.completedLevelsCount)

        collectJob.cancel()
    }

    @Test
    fun uiState_whenRepositoryThrows_updatesToErrorState() = runTest {
        val repository = object : ProfileRepository {
            override fun getUserStats(): Flow<UserProgressStats> = flow {
                throw RuntimeException("Database error")
            }
        }

        val viewModel = ProgressViewModel(repository)
        val collectJob = launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is ProgressUiState.Error)
        val error = state as ProgressUiState.Error
        assertEquals("Database error", error.message)

        collectJob.cancel()
    }
}
