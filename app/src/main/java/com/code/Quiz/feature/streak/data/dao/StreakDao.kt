package com.code.Quiz.feature.streak.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user streak and daily activities.
 */
@Dao
interface StreakDao {
    @Query("SELECT * FROM user_streak WHERE id = 1 LIMIT 1")
    fun getStreakFlow(): Flow<StreakEntity?>

    @Query("SELECT * FROM user_streak WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: StreakEntity)

    @Query("SELECT * FROM daily_activities ORDER BY date DESC LIMIT :limit")
    fun getRecentActivities(limit: Int): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activities WHERE date = :date LIMIT 1")
    suspend fun getActivityByDate(date: String): DailyActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateActivity(activity: DailyActivityEntity)

    @Query("SELECT COALESCE(SUM(xpEarned), 0) FROM daily_activities")
    fun getTotalXp(): Flow<Int>

    @Query("SELECT COALESCE(SUM(exercisesCompleted), 0) FROM daily_activities")
    fun getTotalExercisesCompleted(): Flow<Int>

    @Query("SELECT * FROM daily_activities WHERE date >= :startDate AND date <= :endDate")
    fun getActivitiesBetween(startDate: String, endDate: String): Flow<List<DailyActivityEntity>>
}
