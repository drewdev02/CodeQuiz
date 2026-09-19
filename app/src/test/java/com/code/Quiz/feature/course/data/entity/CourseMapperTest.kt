package com.code.Quiz.feature.course.data.entity

import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseMapperTest {

    @Test
    fun courseEntity_toDomain_and_toEntity_roundTrip() {
        val original = Course(
            id = "course_1",
            title = "Introduction to Programming",
            description = "Learn the basics",
            order = 1,
            badge = "Popular",
            iconType = CourseIconType.ROCKET,
            isUnlocked = true
        )

        val entity = original.toEntity()
        assertEquals("course_1", entity.id)
        assertEquals("Introduction to Programming", entity.title)
        assertEquals("Learn the basics", entity.description)
        assertEquals(1, entity.orderIndex)
        assertEquals("Popular", entity.badge)
        assertEquals("ROCKET", entity.iconType)
        assertTrue(entity.isUnlocked)

        val domain = entity.toDomain()
        assertEquals(original, domain)
    }

    @Test
    fun courseEntity_unknownIconType_fallsBackToRocket() {
        val entity = CourseEntity(
            id = "c1",
            title = "Unknown",
            description = "Desc",
            orderIndex = 1,
            badge = null,
            iconType = "NON_EXISTENT",
            isUnlocked = false
        )

        val domain = entity.toDomain()
        assertEquals(CourseIconType.ROCKET, domain.iconType)
    }

    @Test
    fun courseLevelEntity_toDomain_and_toEntity_roundTrip() {
        val original = CourseLevel(
            id = "lvl_1",
            courseId = "c1",
            levelNumber = 1,
            title = "Variables",
            description = "Learn variables",
            status = LevelStatus.ACTIVE,
            totalExercises = 5,
            xpReward = 50
        )

        val entity = original.toEntity()
        assertEquals("lvl_1", entity.id)
        assertEquals("c1", entity.courseId)
        assertEquals(1, entity.levelNumber)
        assertEquals("Variables", entity.title)
        assertEquals("Learn variables", entity.description)
        assertEquals("ACTIVE", entity.status)
        assertEquals(5, entity.totalExercises)
        assertEquals(50, entity.xpReward)

        val domain = entity.toDomain()
        assertEquals(original, domain)
    }

    @Test
    fun courseLevelEntity_unknownStatus_fallsBackToLocked() {
        val entity = CourseLevelEntity(
            id = "lvl_2",
            courseId = "c1",
            levelNumber = 2,
            title = "Loops",
            description = "Learn loops",
            status = "INVALID_STATUS",
            totalExercises = 3,
            xpReward = 20
        )

        val domain = entity.toDomain()
        assertEquals(LevelStatus.LOCKED, domain.status)
    }
}
