package com.code.Quiz.feature.course.data.repository

import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.domain.model.Course
import com.code.Quiz.feature.course.domain.model.CourseIconType
import com.code.Quiz.feature.course.domain.model.CourseLevel
import com.code.Quiz.feature.course.domain.model.LevelStatus
import com.code.Quiz.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Concrete implementation of [CourseRepository] delivering the curated course roadmap
 * and individual course levels map.
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

    override fun getCourseLevels(courseId: String): Flow<List<CourseLevel>> = flow {
        val levels = when (courseId) {
            "intro_programming" -> listOf(
                CourseLevel(
                    id = "intro_prog_level_1",
                    courseId = courseId,
                    levelNumber = 1,
                    title = "Variables y Tipos de Datos",
                    description = "Aprende qué es una variable y cómo almacenar datos en memoria.",
                    status = LevelStatus.COMPLETED,
                    totalExercises = 5,
                    xpReward = 30
                ),
                CourseLevel(
                    id = "intro_prog_level_2",
                    courseId = courseId,
                    levelNumber = 2,
                    title = "Operadores y Expresiones",
                    description = "Operaciones aritméticas, lógicas y de comparación para transformar datos.",
                    status = LevelStatus.COMPLETED,
                    totalExercises = 5,
                    xpReward = 35
                ),
                CourseLevel(
                    id = "intro_prog_level_3",
                    courseId = courseId,
                    levelNumber = 3,
                    title = "Estructuras Condicionales",
                    description = "Toma decisiones en tu código evaluando expresiones booleanas con if, else y when.",
                    status = LevelStatus.ACTIVE,
                    totalExercises = 6,
                    xpReward = 40
                ),
                CourseLevel(
                    id = "intro_prog_level_4",
                    courseId = courseId,
                    levelNumber = 4,
                    title = "Bucles y Ciclos",
                    description = "Repite instrucciones eficientemente con bucles while y for.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 6,
                    xpReward = 45
                ),
                CourseLevel(
                    id = "intro_prog_level_5",
                    courseId = courseId,
                    levelNumber = 5,
                    title = "Funciones y Modularidad",
                    description = "Divide tu código en bloques reutilizables, parámetros y retornos limpios.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 7,
                    xpReward = 50
                ),
                CourseLevel(
                    id = "intro_prog_level_6",
                    courseId = courseId,
                    levelNumber = 6,
                    title = "Arreglos y Colecciones",
                    description = "Almacena conjuntos de elementos y accede a ellos por índice.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 8,
                    xpReward = 60
                ),
                CourseLevel(
                    id = "intro_prog_level_7",
                    courseId = courseId,
                    levelNumber = 7,
                    title = "Algoritmos y Reto Final",
                    description = "Pon a prueba todo lo aprendido en el examen integrador de lógica algorítmica.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 10,
                    xpReward = 100
                )
            )
            "kotlin_basics" -> listOf(
                CourseLevel(
                    id = "kotlin_level_1",
                    courseId = courseId,
                    levelNumber = 1,
                    title = "Sintaxis Básica y Variables",
                    description = "Inmutabilidad con val vs var y tipos primitivos en Kotlin.",
                    status = LevelStatus.COMPLETED,
                    totalExercises = 5,
                    xpReward = 30
                ),
                CourseLevel(
                    id = "kotlin_level_2",
                    courseId = courseId,
                    levelNumber = 2,
                    title = "Null Safety y Safe Calls",
                    description = "El sistema de tipos no nulables de Kotlin y operador Elvis.",
                    status = LevelStatus.ACTIVE,
                    totalExercises = 5,
                    xpReward = 35
                ),
                CourseLevel(
                    id = "kotlin_level_3",
                    courseId = courseId,
                    levelNumber = 3,
                    title = "Funciones y Lambdas",
                    description = "Parámetros nombrados, valores por defecto y funciones de orden superior.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 6,
                    xpReward = 40
                ),
                CourseLevel(
                    id = "kotlin_level_4",
                    courseId = courseId,
                    levelNumber = 4,
                    title = "Clases de Datos y Sealed Types",
                    description = "Modelado de datos robusto con data class y sealed interfaces.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 6,
                    xpReward = 50
                )
            )
            else -> listOf(
                CourseLevel(
                    id = "${courseId}_level_1",
                    courseId = courseId,
                    levelNumber = 1,
                    title = "Fundamentos Iniciales",
                    description = "Primeros conceptos y base teórica interactiva.",
                    status = LevelStatus.ACTIVE,
                    totalExercises = 5,
                    xpReward = 30
                ),
                CourseLevel(
                    id = "${courseId}_level_2",
                    courseId = courseId,
                    levelNumber = 2,
                    title = "Práctica Intermedia",
                    description = "Ejercicios guiados para afianzar el conocimiento.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 6,
                    xpReward = 40
                ),
                CourseLevel(
                    id = "${courseId}_level_3",
                    courseId = courseId,
                    levelNumber = 3,
                    title = "Desafío Final",
                    description = "Examen integrador del curso.",
                    status = LevelStatus.LOCKED,
                    totalExercises = 8,
                    xpReward = 60
                )
            )
        }
        emit(levels)
    }.flowOn(dispatcherProvider.io)
}

