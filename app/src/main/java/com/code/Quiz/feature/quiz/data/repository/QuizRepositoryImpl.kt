package com.code.Quiz.feature.quiz.data.repository

import com.code.Quiz.core.util.DefaultDispatcherProvider
import com.code.Quiz.core.util.DispatcherProvider
import com.code.Quiz.feature.course.data.dao.CourseLevelDao
import com.code.Quiz.feature.course.data.seeder.DatabaseSeeder
import com.code.Quiz.feature.quiz.data.dao.ExerciseDao
import com.code.Quiz.feature.quiz.data.entity.toDomain
import com.code.Quiz.feature.quiz.domain.model.Exercise
import com.code.Quiz.feature.quiz.domain.model.ExerciseOption
import com.code.Quiz.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of QuizRepository for loading exercises and updating level progression.
 */
class QuizRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val courseLevelDao: CourseLevelDao,
    private val databaseSeeder: DatabaseSeeder? = null,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : QuizRepository {

    override fun getExercisesForLevel(levelId: String): Flow<List<Exercise>> {
        return flow {
            databaseSeeder?.seedIfNeeded()
            emitAll(
                exerciseDao.getExercisesForLevel(levelId).map { entities ->
                    if (entities.isNotEmpty()) {
                        entities.map { it.toDomain() }
                    } else {
                        generateFallbackExercises(levelId)
                    }
                }
            )
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun completeLevel(courseId: String, levelId: String, xpEarned: Int) =
        withContext(dispatcherProvider.io) {
            // 1. Mark current level as COMPLETED
            courseLevelDao.updateLevelStatus(levelId, "COMPLETED")

            // 2. Find current level to identify its course and level number
            val currentLevel = courseLevelDao.getLevelById(levelId)
            val effectiveCourseId = if (courseId.isNotBlank()) courseId else (currentLevel?.courseId ?: "")

            if (effectiveCourseId.isNotBlank()) {
                val courseLevels = courseLevelDao.getLevelsByCourse(effectiveCourseId)
                val currentNumber = currentLevel?.levelNumber
                    ?: courseLevels.firstOrNull { it.id == levelId }?.levelNumber

                if (currentNumber != null) {
                    val nextLevel = courseLevels.firstOrNull { it.levelNumber == currentNumber + 1 }
                    if (nextLevel != null && nextLevel.status == "LOCKED") {
                        courseLevelDao.updateLevelStatus(nextLevel.id, "ACTIVE")
                    }
                }
            }
        }

    private fun generateFallbackExercises(levelId: String): List<Exercise> {
        return listOf(
            Exercise(
                id = "${levelId}_fallback_1",
                levelId = levelId,
                question = "¿Cuál es la palabra clave en Kotlin para declarar una variable inmutable de solo lectura?",
                codeSnippet = "val language = \"Kotlin\"\n// language = \"Java\" // Error",
                language = "Kotlin",
                options = listOf(
                    ExerciseOption("opt_1", "var", false),
                    ExerciseOption("opt_2", "val", true),
                    ExerciseOption("opt_3", "let", false),
                    ExerciseOption("opt_4", "const", false)
                ),
                explanation = "En Kotlin, 'val' declara variables inmutables de solo lectura, mientras que 'var' declara variables mutables."
            ),
            Exercise(
                id = "${levelId}_fallback_2",
                levelId = levelId,
                question = "¿Qué imprimirá en consola este código?",
                codeSnippet = "var score = 10\nscore += 5\nprintln(score)",
                language = "Kotlin",
                options = listOf(
                    ExerciseOption("opt_1", "10", false),
                    ExerciseOption("opt_2", "15", true),
                    ExerciseOption("opt_3", "5", false),
                    ExerciseOption("opt_4", "score", false)
                ),
                explanation = "Al ser mutable con 'var', 'score' se actualiza con '+=' sumando 5 a 10, imprimiendo 15."
            ),
            Exercise(
                id = "${levelId}_fallback_3",
                levelId = levelId,
                question = "¿Cuál es el resultado de la siguiente expresión booleana?",
                codeSnippet = "val result = (10 > 5) && (3 == 3)\nprintln(result)",
                language = "Kotlin",
                options = listOf(
                    ExerciseOption("opt_1", "false", false),
                    ExerciseOption("opt_2", "true", true),
                    ExerciseOption("opt_3", "null", false),
                    ExerciseOption("opt_4", "0", false)
                ),
                explanation = "Ambas condiciones son verdaderas (10 > 5 es true y 3 == 3 es true), por lo que el operador '&&' evalúa a true."
            ),
            Exercise(
                id = "${levelId}_fallback_4",
                levelId = levelId,
                question = "¿Cómo se accede de manera segura a una propiedad potencialmente nula en Kotlin?",
                codeSnippet = "val length = name?.length",
                language = "Kotlin",
                options = listOf(
                    ExerciseOption("opt_1", "name!!.length", false),
                    ExerciseOption("opt_2", "name?.length", true),
                    ExerciseOption("opt_3", "name:length", false),
                    ExerciseOption("opt_4", "name->length", false)
                ),
                explanation = "El operador safe call '?.' permite invocar miembros de manera segura sin riesgo de NullPointerException."
            ),
            Exercise(
                id = "${levelId}_fallback_5",
                levelId = levelId,
                question = "¿Qué función de orden superior se usa para transformar los elementos de una colección en Kotlin?",
                codeSnippet = "val doubled = numbers.map { it * 2 }",
                language = "Kotlin",
                options = listOf(
                    ExerciseOption("opt_1", "filter", false),
                    ExerciseOption("opt_2", "map", true),
                    ExerciseOption("opt_3", "forEach", false),
                    ExerciseOption("opt_4", "reduce", false)
                ),
                explanation = "La función 'map' transforma cada elemento de una colección aplicando la lambda dada y retorna una nueva lista con los resultados."
            )
        )
    }
}
