package com.code.Quiz.di

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.core.util.DispatcherProvider
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
