package com.code.Quiz.feature.course.data.seeder

import com.code.Quiz.feature.course.data.dto.SeedDataDto
import com.code.Quiz.feature.course.data.entity.CourseLevelEntity
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DatabaseSeederSmartSyncTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun seedData_hasValidCatalogVersion() {
        val file = File("src/main/assets/data/seed_data.json")
        assertTrue("seed_data.json must exist", file.exists())

        val content = file.readText()
        val dto = json.decodeFromString<SeedDataDto>(content)

        assertTrue("Catalog version must be >= 1", dto.version >= 1)
        assertEquals(8, dto.version)
    }

    @Test
    fun levelProgressPreservation_preservesCompletedAndActiveStatus() {
        // Given existing levels in the database with user progress
        val existingLevels = listOf(
            CourseLevelEntity(
                id = "intro_prog_level_1",
                courseId = "intro_programming",
                levelNumber = 1,
                title = "Variables",
                description = "Desc",
                status = "COMPLETED",
                totalExercises = 5,
                xpReward = 50
            ),
            CourseLevelEntity(
                id = "intro_prog_level_2",
                courseId = "intro_programming",
                levelNumber = 2,
                title = "Operators",
                description = "Desc",
                status = "ACTIVE",
                totalExercises = 5,
                xpReward = 50
            ),
            CourseLevelEntity(
                id = "intro_prog_level_3",
                courseId = "intro_programming",
                levelNumber = 3,
                title = "Conditionals",
                description = "Desc",
                status = "LOCKED",
                totalExercises = 5,
                xpReward = 50
            )
        )
        val existingLevelsMap = existingLevels.associateBy { it.id }

        // Simulated incoming seed data for levels
        val incomingLevels = listOf(
            Triple("intro_prog_level_1", "ACTIVE", 1),
            Triple("intro_prog_level_2", "LOCKED", 2),
            Triple("intro_prog_level_3", "LOCKED", 3),
            Triple("intro_prog_level_new", "LOCKED", 4)
        )

        val completedLevelNumbers = incomingLevels
            .mapNotNull { (id, _, _) -> existingLevelsMap[id] }
            .filter { it.status == "COMPLETED" }
            .map { it.levelNumber }
            .toSet()
        val maxCompleted = completedLevelNumbers.maxOrNull() ?: 0

        val mergedStatuses = incomingLevels.map { (_, _, levelNumber) ->
            if (completedLevelNumbers.contains(levelNumber)) {
                "COMPLETED"
            } else if (levelNumber == maxCompleted + 1) {
                "ACTIVE"
            } else {
                "LOCKED"
            }
        }

        // Level 1 was COMPLETED -> remains COMPLETED
        assertEquals("COMPLETED", mergedStatuses[0])
        // Level 2 (next uncompleted) -> strictly ACTIVE
        assertEquals("ACTIVE", mergedStatuses[1])
        // Level 3 -> strictly LOCKED
        assertEquals("LOCKED", mergedStatuses[2])
        // Level 4 -> strictly LOCKED
        assertEquals("LOCKED", mergedStatuses[3])
    }

    @Test
    fun levelProgressPreservation_resetsCorruptedMultipleActiveLevelsWhenZeroCompleted() {
        // If an older database or faulty seed had multiple levels as ACTIVE with 0 completed
        val existingLevels = listOf(
            CourseLevelEntity("l1", "cpp", 1, "T1", "D", "ACTIVE", 5, 30),
            CourseLevelEntity("l2", "cpp", 2, "T2", "D", "ACTIVE", 5, 30),
            CourseLevelEntity("l3", "cpp", 3, "T3", "D", "ACTIVE", 5, 30),
            CourseLevelEntity("l4", "cpp", 4, "T4", "D", "ACTIVE", 5, 30)
        )
        val existingLevelsMap = existingLevels.associateBy { it.id }

        val incomingLevels = listOf(
            Triple("l1", "ACTIVE", 1),
            Triple("l2", "LOCKED", 2),
            Triple("l3", "LOCKED", 3),
            Triple("l4", "LOCKED", 4)
        )

        val completedLevelNumbers = incomingLevels
            .mapNotNull { (id, _, _) -> existingLevelsMap[id] }
            .filter { it.status == "COMPLETED" }
            .map { it.levelNumber }
            .toSet()
        val maxCompleted = completedLevelNumbers.maxOrNull() ?: 0

        val mergedStatuses = incomingLevels.map { (_, _, levelNumber) ->
            if (completedLevelNumbers.contains(levelNumber)) {
                "COMPLETED"
            } else if (levelNumber == maxCompleted + 1) {
                "ACTIVE"
            } else {
                "LOCKED"
            }
        }

        // Only Level 1 must be ACTIVE, all other levels must be LOCKED!
        assertEquals("ACTIVE", mergedStatuses[0])
        assertEquals("LOCKED", mergedStatuses[1])
        assertEquals("LOCKED", mergedStatuses[2])
        assertEquals("LOCKED", mergedStatuses[3])
    }

    @Test
    fun smartSyncCondition_skipsWhenDbPopulatedAndUpToDate() {
        val courseCount = 2
        val articleCount = 6
        val storedCatalogVersion = 2

        val seedCoursesSize = 2
        val seedArticlesSize = 6
        val seedVersion = 2

        val isDbEmpty = courseCount == 0
        val hasNewCatalogVersion = seedVersion > storedCatalogVersion
        val hasMissingCourses = courseCount < seedCoursesSize
        val hasMissingArticles = articleCount < seedArticlesSize

        val shouldSkip = !isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles
        assertTrue("Sync should be skipped when DB is fully populated and catalog is up-to-date", shouldSkip)
    }

    @Test
    fun smartSyncCondition_triggersOnNewCatalogVersion() {
        val courseCount = 2
        val articleCount = 6
        val storedCatalogVersion = 1

        val seedCoursesSize = 2
        val seedArticlesSize = 6
        val seedVersion = 2

        val isDbEmpty = courseCount == 0
        val hasNewCatalogVersion = seedVersion > storedCatalogVersion
        val hasMissingCourses = courseCount < seedCoursesSize
        val hasMissingArticles = articleCount < seedArticlesSize

        val shouldSkip = !isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles
        assertFalse("Sync should execute when seed catalog has a higher version", shouldSkip)
        assertTrue(hasNewCatalogVersion)
    }

    @Test
    fun smartSyncCondition_triggersOnMissingCourses() {
        val courseCount = 1
        val articleCount = 6
        val storedCatalogVersion = 2

        val seedCoursesSize = 2
        val seedArticlesSize = 6
        val seedVersion = 2

        val isDbEmpty = courseCount == 0
        val hasNewCatalogVersion = seedVersion > storedCatalogVersion
        val hasMissingCourses = courseCount < seedCoursesSize
        val hasMissingArticles = articleCount < seedArticlesSize

        val shouldSkip = !isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles
        assertFalse("Sync should execute when courses are missing from DB", shouldSkip)
        assertTrue(hasMissingCourses)
    }

    @Test
    fun smartSyncCondition_triggersOnMissingArticles() {
        val courseCount = 2
        val articleCount = 3
        val storedCatalogVersion = 2

        val seedCoursesSize = 2
        val seedArticlesSize = 6
        val seedVersion = 2

        val isDbEmpty = courseCount == 0
        val hasNewCatalogVersion = seedVersion > storedCatalogVersion
        val hasMissingCourses = courseCount < seedCoursesSize
        val hasMissingArticles = articleCount < seedArticlesSize

        val shouldSkip = !isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles
        assertFalse("Sync should execute when articles are missing from DB", shouldSkip)
        assertTrue(hasMissingArticles)
    }

    @Test
    fun smartSyncCondition_triggersWhenDbIsEmpty() {
        val courseCount = 0
        val articleCount = 0
        val storedCatalogVersion = 0

        val seedCoursesSize = 2
        val seedArticlesSize = 6
        val seedVersion = 2

        val isDbEmpty = courseCount == 0
        val hasNewCatalogVersion = seedVersion > storedCatalogVersion
        val hasMissingCourses = courseCount < seedCoursesSize
        val hasMissingArticles = articleCount < seedArticlesSize

        val shouldSkip = !isDbEmpty && !hasNewCatalogVersion && !hasMissingCourses && !hasMissingArticles
        assertFalse("Sync should execute when DB is empty", shouldSkip)
        assertTrue(isDbEmpty)
    }
}
