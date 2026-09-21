package com.code.Quiz.feature.streak.data.mapper

import com.code.Quiz.feature.streak.data.entity.DailyActivityEntity
import com.code.Quiz.feature.streak.data.entity.StreakEntity
import com.code.Quiz.feature.streak.domain.model.DailyActivity
import com.code.Quiz.feature.streak.domain.model.Streak

fun StreakEntity.toDomain(isActiveToday: Boolean, effectiveStreak: Int): Streak {
    return Streak(
        currentStreak = effectiveStreak,
        maxStreak = this.maxStreak,
        lastActiveDate = this.lastActiveDate,
        isActiveToday = isActiveToday
    )
}

fun DailyActivityEntity.toDomain(): DailyActivity {
    return DailyActivity(
        date = this.date,
        xpEarned = this.xpEarned,
        exercisesCompleted = this.exercisesCompleted
    )
}

fun DailyActivity.toEntity(): DailyActivityEntity {
    return DailyActivityEntity(
        date = this.date,
        xpEarned = this.xpEarned,
        exercisesCompleted = this.exercisesCompleted
    )
}
