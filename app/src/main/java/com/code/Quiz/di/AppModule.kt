package com.code.Quiz.di

import com.code.Quiz.feature.course.di.courseModule
import com.code.Quiz.feature.library.di.libraryModule
import com.code.Quiz.feature.profile.di.profileModule
import com.code.Quiz.feature.quiz.di.quizModule
import com.code.Quiz.feature.streak.di.streakModule

val appModules = listOf(
    coreModule,
    courseModule,
    quizModule,
    libraryModule,
    streakModule,
    profileModule
)
