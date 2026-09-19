package com.code.Quiz.feature.quiz.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal data class ExerciseOptionDto(
    val id: String,
    val text: String,
    val isCorrect: Boolean
)

private val jsonSerializer = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

/**
 * Room Entity representing an exercise micro-challenge.
 */
@Entity(
    tableName = "exercises",
    indices = [Index("levelId")]
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val levelId: String,
    val question: String,
    val codeSnippet: String? = null,
    val language: String = "Kotlin",
    val optionsJson: String,
    val explanation: String
)

/**
 * Mapper extension to convert ExerciseEntity to pure domain Exercise model.
 */
fun ExerciseEntity.toDomain(): Exercise {
    val optionsList: List<ExerciseOption> = try {
        val dtoList = jsonSerializer.decodeFromString<List<ExerciseOptionDto>>(optionsJson)
        dtoList.map { dto ->
            ExerciseOption(
                id = dto.id,
                text = dto.text,
                isCorrect = dto.isCorrect
            )
        }
    } catch (_: Exception) {
        emptyList()
    }

    return Exercise(
        id = id,
        levelId = levelId,
        question = question,
        codeSnippet = codeSnippet,
        language = language,
        options = optionsList,
        explanation = explanation
    )
}

/**
 * Mapper extension to convert pure domain Exercise model to ExerciseEntity.
 */
fun Exercise.toEntity(): ExerciseEntity {
    val dtoList = options.map { opt ->
        ExerciseOptionDto(
            id = opt.id,
            text = opt.text,
            isCorrect = opt.isCorrect
        )
    }
    val jsonString = jsonSerializer.encodeToString(dtoList)

    return ExerciseEntity(
        id = id,
        levelId = levelId,
        question = question,
        codeSnippet = codeSnippet,
        language = language,
        optionsJson = jsonString,
        explanation = explanation
    )
}
