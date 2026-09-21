package com.code.Quiz.feature.course.di

import com.code.Quiz.feature.course.data.repository.CourseRepositoryImpl
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import com.code.Quiz.feature.course.ui.CourseLevelsViewModel
import com.code.Quiz.feature.course.ui.CourseViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val courseModule = module {
    single {
        DatabaseSeeder(
            context = androidContext(),
            database = get(),
            courseDao = get(),
            courseLevelDao = get(),
            exerciseDao = get(),
            articleDao = get(),
            streakDao = get(),
            dispatcherProvider = get()
        )
    }

    single<CourseRepository> {
        CourseRepositoryImpl(
            courseDao = get(),
            courseLevelDao = get(),
            databaseSeeder = get(),
            dispatcherProvider = get()
        )
    }

    viewModelOf(::CourseViewModel)
    viewModelOf(::CourseLevelsViewModel)
}
