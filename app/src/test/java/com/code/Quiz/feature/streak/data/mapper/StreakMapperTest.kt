package com.code.Quiz.feature.streak.data.mapper

import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import com.code.Quiz.feature.streak.domain.model.DailyActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakMapperTest {

    @Test
    fun streakEntityToDomain_mapsCorrectly() {
        val entity = StreakEntity(
            id = 1,
            currentStreak = 5,
            maxStreak = 10,
            lastActiveDate = "2026-09-20"
        )

        val domainActive = entity.toDomain(isActiveToday = true, effectiveStreak = 5)
        assertEquals(5, domainActive.currentStreak)
        assertEquals(10, domainActive.maxStreak)
        assertEquals("2026-09-20", domainActive.lastActiveDate)
        assertTrue(domainActive.isActiveToday)

        val domainBroken = entity.toDomain(isActiveToday = false, effectiveStreak = 0)
        assertEquals(0, domainBroken.currentStreak)
        assertEquals(10, domainBroken.maxStreak)
        assertFalse(domainBroken.isActiveToday)
    }

    @Test
    fun dailyActivityEntityToDomainAndReverse_mapsCorrectly() {
        val entity = DailyActivityEntity(
            date = "2026-09-20",
            xpEarned = 50,
            exercisesCompleted = 6
        )

        val domain = entity.toDomain()
        assertEquals("2026-09-20", domain.date)
        assertEquals(50, domain.xpEarned)
        assertEquals(6, domain.exercisesCompleted)

        val mappedEntity = domain.toEntity()
        assertEquals(entity.date, mappedEntity.date)
        assertEquals(entity.xpEarned, mappedEntity.xpEarned)
        assertEquals(entity.exercisesCompleted, mappedEntity.exercisesCompleted)
    }
}
