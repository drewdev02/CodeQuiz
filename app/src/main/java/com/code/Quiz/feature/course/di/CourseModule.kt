package com.code.Quiz.feature.course.di

import com.code.Quiz.feature.course.data.repository.CourseRepositoryImpl
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import com.code.Quiz.feature.course.ui.CourseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val courseModule = module {
    single<CourseRepository> { CourseRepositoryImpl(dispatcherProvider = get()) }
    viewModelOf(::CourseViewModel)
}
