package com.code.Quiz.feature.course.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Concrete implementation of [CourseRepository] delivering the curated course roadmap.
 * Executes on [DispatcherProvider.io] per clean architecture standards.
 */
class CourseRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider
) : CourseRepository {

    override fun getCoursePath(): Flow<List<Course>> = flow {
        emit(
            listOf(
                Course(
                    id = "intro_programming",
                    title = "Curso de introducción a la programación",
                    description = "Aprende los fundamentos de la computación, pensamiento lógico, variables y estructuras de control.",
                    order = 1,
                    badge = "Curso Top",
                    iconType = CourseIconType.ROCKET,
                    isUnlocked = true
                ),
                Course(
                    id = "kotlin_basics",
                    title = "Fundamentos de Kotlin",
                    description = "Sintaxis moderna, inmutabilidad, funciones, clases selladas y null safety sin rodeos.",
                    order = 2,
                    badge = "Recomendado",
                    iconType = CourseIconType.CODE,
                    isUnlocked = true
                ),
                Course(
                    id = "data_structures",
                    title = "Estructuras de Datos Básicas",
                    description = "Listas, pilas, colas, mapas y árboles explicados con retos visuales e interactivos.",
                    order = 3,
                    badge = null,
                    iconType = CourseIconType.DATABASE,
                    isUnlocked = true
                ),
                Course(
                    id = "algorithms_logic",
                    title = "Algoritmos y Resolución de Problemas",
                    description = "Búsquedas, ordenamientos y análisis de complejidad de forma práctica.",
                    order = 4,
                    badge = "Avanzado",
                    iconType = CourseIconType.TERMINAL,
                    isUnlocked = true
                )
            )
        )
    }.flowOn(dispatcherProvider.io)
}
