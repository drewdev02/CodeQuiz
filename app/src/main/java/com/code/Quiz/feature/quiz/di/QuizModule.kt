package com.code.Quiz.feature.quiz.di

import com.code.Quiz.feature.quiz.data.repository.QuizRepositoryImpl
import com.code.Quiz.feature.quiz.domain.repository.QuizRepository
import com.code.Quiz.feature.quiz.domain.usecase.CompleteLevelUseCase
import com.code.Quiz.feature.quiz.domain.usecase.EvaluateAnswerUseCase
import com.code.Quiz.feature.quiz.domain.usecase.GetLevelExercisesUseCase
import com.code.Quiz.feature.quiz.ui.QuizViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin DI module for the Quiz / Level micro-challenge feature.
 */
val quizModule = module {
    single<QuizRepository> {
        QuizRepositoryImpl(
            exerciseDao = get(),
            courseLevelDao = get(),
            databaseSeeder = get(),
            dispatcherProvider = get()
        )
    }

    factory { GetLevelExercisesUseCase(quizRepository = get()) }
    factory { CompleteLevelUseCase(quizRepository = get()) }
    factory { EvaluateAnswerUseCase() }

    viewModelOf(::QuizViewModel)
}
