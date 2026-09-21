package com.code.Quiz.feature.streak.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.streak.data.dao.StreakDao
import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class StreakRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeStreakDao: FakeStreakDao
    private lateinit var repository: StreakRepositoryImpl

    private class TestDispatcherProvider(
        private val dispatcher: CoroutineDispatcher
    ) : DispatcherProvider {
        override val main: CoroutineDispatcher get() = dispatcher
        override val io: CoroutineDispatcher get() = dispatcher
        override val default: CoroutineDispatcher get() = dispatcher
        override val unconfined: CoroutineDispatcher get() = dispatcher
    }

    @Before
    fun setUp() {
        fakeStreakDao = FakeStreakDao()
        repository = StreakRepositoryImpl(
            streakDao = fakeStreakDao,
            dispatcherProvider = TestDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun getStreak_whenNoEntityInDb_returnsZeroStreakNotActiveToday() = runTest(testDispatcher) {
        val streak = repository.getStreak().first()
        assertEquals(0, streak.currentStreak)
        assertEquals(0, streak.maxStreak)
        assertFalse(streak.isActiveToday)
    }

    @Test
    fun recordActivity_firstTimeToday_setsStreakToOneAndRecordsActivity() = runTest(testDispatcher) {
        repository.recordActivity(xpEarned = 30, exercisesCount = 3)

        val streak = repository.getStreak().first()
        val todayStr = LocalDate.now().toString()

        assertEquals(1, streak.currentStreak)
        assertEquals(1, streak.maxStreak)
        assertEquals(todayStr, streak.lastActiveDate)
        assertTrue(streak.isActiveToday)

        val activity = fakeStreakDao.getActivityByDate(todayStr)
        assertEquals(30, activity?.xpEarned)
        assertEquals(3, activity?.exercisesCompleted)
    }

    @Test
    fun recordActivity_sameDayTwice_accumulatesActivityWithoutIncrementingStreakTwice() = runTest(testDispatcher) {
        repository.recordActivity(xpEarned = 30, exercisesCount = 3)
        repository.recordActivity(xpEarned = 20, exercisesCount = 2)

        val streak = repository.getStreak().first()
        assertEquals(1, streak.currentStreak)
        assertEquals(1, streak.maxStreak)
        assertTrue(streak.isActiveToday)

        val todayStr = LocalDate.now().toString()
        val activity = fakeStreakDao.getActivityByDate(todayStr)
        assertEquals(50, activity?.xpEarned)
        assertEquals(5, activity?.exercisesCompleted)
    }

    @Test
    fun recordActivity_whenLastActiveYesterday_incrementsCurrentStreak() = runTest(testDispatcher) {
        val yesterdayStr = LocalDate.now().minusDays(1).toString()
        fakeStreakDao.insertOrUpdateStreak(
            StreakEntity(id = 1, currentStreak = 3, maxStreak = 5, lastActiveDate = yesterdayStr)
        )

        repository.recordActivity(xpEarned = 40, exercisesCount = 4)

        val streak = repository.getStreak().first()
        assertEquals(4, streak.currentStreak)
        assertEquals(5, streak.maxStreak)
        assertTrue(streak.isActiveToday)
    }

    @Test
    fun recordActivity_whenStreakBroken_resetsCurrentStreakToOneAndKeepsMaxStreak() = runTest(testDispatcher) {
        val threeDaysAgoStr = LocalDate.now().minusDays(3).toString()
        fakeStreakDao.insertOrUpdateStreak(
            StreakEntity(id = 1, currentStreak = 10, maxStreak = 15, lastActiveDate = threeDaysAgoStr)
        )

        repository.recordActivity(xpEarned = 25, exercisesCount = 2)

        val streak = repository.getStreak().first()
        assertEquals(1, streak.currentStreak)
        assertEquals(15, streak.maxStreak)
        assertTrue(streak.isActiveToday)
    }

    @Test
    fun getWeekStreak_returnsSevenDaysWithCorrectLabelsAndTodayFlag() = runTest(testDispatcher) {
        val week = repository.getWeekStreak().first()
        assertEquals(7, week.size)

        val expectedLabels = listOf("L", "M", "M", "J", "V", "S", "D")
        val actualLabels = week.map { it.dayOfWeek }
        assertEquals(expectedLabels, actualLabels)

        val todayCount = week.count { it.isToday }
        assertEquals(1, todayCount)
    }

    private class FakeStreakDao : StreakDao {
        var streakEntity: StreakEntity? = null
        val activities = mutableMapOf<String, DailyActivityEntity>()
        private val streakFlow = MutableStateFlow<StreakEntity?>(null)
        private val activitiesFlow = MutableStateFlow<List<DailyActivityEntity>>(emptyList())

        override fun getStreakFlow(): Flow<StreakEntity?> = streakFlow

        override suspend fun getStreak(): StreakEntity? = streakEntity

        override suspend fun insertOrUpdateStreak(streak: StreakEntity) {
            streakEntity = streak
            streakFlow.value = streak
        }

        override fun getRecentActivities(limit: Int): Flow<List<DailyActivityEntity>> =
            activitiesFlow.map { list -> list.sortedByDescending { it.date }.take(limit) }

        override suspend fun getActivityByDate(date: String): DailyActivityEntity? = activities[date]

        override suspend fun insertOrUpdateActivity(activity: DailyActivityEntity) {
            activities[activity.date] = activity
            activitiesFlow.value = activities.values.toList()
        }

        override fun getTotalXp(): Flow<Int> =
            activitiesFlow.map { it.sumOf { a -> a.xpEarned } }

        override fun getTotalExercisesCompleted(): Flow<Int> =
            activitiesFlow.map { it.sumOf { a -> a.exercisesCompleted } }

        override fun getActivitiesBetween(startDate: String, endDate: String): Flow<List<DailyActivityEntity>> =
            activitiesFlow.map { list ->
                list.filter { it.date in startDate..endDate }
            }
    }
}
