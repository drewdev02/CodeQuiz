package com.code.Quiz.feature.quiz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.Quiz.feature.quiz.data.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for persisting and querying exercises in Room.
 */
@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE levelId = :levelId")
    fun getExercisesForLevel(levelId: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}
