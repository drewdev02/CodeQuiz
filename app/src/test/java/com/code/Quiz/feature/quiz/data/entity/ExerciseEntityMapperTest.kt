package com.code.Quiz.feature.quiz.data.entity

import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseDifficulty
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseEntityMapperTest {

    @Test
    fun toDomain_mapsKnownDifficultiesCorrectly() {
        val easyEntity = ExerciseEntity(
            id = "ex_1",
            levelId = "lvl_1",
            question = "Pregunta fácil",
            optionsJson = """[{"id":"opt1","text":"A","isCorrect":true}]""",
            explanation = "Exp",
            difficulty = "EASY"
        )
        val mediumEntity = easyEntity.copy(id = "ex_2", difficulty = "MEDIUM")
        val hardEntity = easyEntity.copy(id = "ex_3", difficulty = "HARD")
        val unknownEntity = easyEntity.copy(id = "ex_4", difficulty = "INVALID_DIFFICULTY")

        assertEquals(ExerciseDifficulty.EASY, easyEntity.toDomain().difficulty)
        assertEquals(ExerciseDifficulty.MEDIUM, mediumEntity.toDomain().difficulty)
        assertEquals(ExerciseDifficulty.HARD, hardEntity.toDomain().difficulty)
        assertEquals(ExerciseDifficulty.EASY, unknownEntity.toDomain().difficulty)
    }

    @Test
    fun toEntity_mapsDifficultyToString() {
        val domainExercise = Exercise(
            id = "ex_10",
            levelId = "lvl_1",
            question = "¿Cuál es difícil?",
            options = listOf(ExerciseOption("opt1", "X", true)),
            explanation = "Exp",
            difficulty = ExerciseDifficulty.HARD
        )

        val entity = domainExercise.toEntity()
        assertEquals("HARD", entity.difficulty)
    }
}
