# Plan de Implementación — Integración Curricular C++, Indicadores de Dificultad y Expansión del Catálogo

> **Para ejecutores y agentes:** HABILIDAD REQUERIDA: Usar `superpowers:subagent-driven-development` (recomendado) o `superpowers:executing-plans` para implementar este plan tarea por tarea. Cada paso utiliza la sintaxis de casilla de verificación (`- [ ]`) para su seguimiento.

**Meta:** Integrar los ~80 micro-retos pedagógicos de C++ contenidos en `~/Downloads/apk.txt` al catálogo de `seed_data.json` de CodeQuiz sin eliminar los existentes, incorporando un sistema formal de indicadores de dificultad (**Fácil, Medio y Difícil**) en el modelo de datos, la interfaz de usuario (`QuizScreen`, `LevelDetailBottomSheet`) y la documentación técnica.

**Arquitectura:** 
1. **Dominio & Persistencia:** Nuevo enum `ExerciseDifficulty` (`EASY`, `MEDIUM`, `HARD`) integrado en `Exercise`, `ExerciseEntity` (Room) y `ExerciseDto` (`kotlinx.serialization`).
2. **Sistema de Diseño (Design System):** Componente unificado `DifficultyBadge` en `core/ui/component/badge/CodeQuizBadge.kt` utilizando la paleta oficial de `DESIGN.md` (`SuccessMint`, `StreakOrange`, `AccentPurple`).
3. **Presentación (UI/UX):** Badge visible en cada pregunta de `QuizScreen` y desglose de curva de dificultad en `LevelDetailBottomSheet`.
4. **Catálogo & Sincronización:** Extractor en Python con etiquetado automático de dificultad, fusión sin pérdidas en `seed_data.json` y salto a `version = 7` para actualización atómica vía `Smart Sync` de Room.

**Tech Stack:** Kotlin 2.2, Jetpack Compose (Material 3), Room Database, Python 3, JUnit4.

**Especificación:** [`PROJECT.md`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/PROJECT.md), [`DESIGN.md`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/DESIGN.md), [`AUDIT.md`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/AUDIT.md) y archivo fuente [`/Users/Andrew/Downloads/apk.txt`](file:///Users/Andrew/Downloads/apk.txt).

---

## Restricciones Globales

1. **Cero pérdida de contenido:** Los ejercicios preexistentes en `cpp_linear_programming` y `java_oop_mastery` se conservan y se les asigna su respectivo nivel de dificultad.
2. **Fidelidad textual (*Tal cual*):** El enunciado, código C++, opciones y retroalimentación de `apk.txt` se transcriben textualmente sin alterar el rigor pedagógico.
3. **Reglas de diseño de tokens:** Utilizar exclusivamente tokens de color oficiales (`SuccessMint` para Fácil, `StreakOrange` para Medio, `AccentPurple` para Difícil) y tokens de espaciado `Spacing.*`.
4. **Sincronización Room limpia:** Incrementar `version` a `7` en `seed_data.json` para que el `DatabaseSeeder` sincronice automáticamente los nuevos retos en el dispositivo preservando el progreso del usuario.
5. **No rotura de pruebas:** Todos los tests preexistentes y nuevos deben compilar y pasar al 100% (`./gradlew testDebugUnitTest`).

---

## Foco de Revisión (Casos Críticos y Modos de Falla)

1. **Serialización y Mapeo de Dificultad:** Un ejercicio con dificultad no reconocida en JSON debe fallar de forma segura retornando `ExerciseDifficulty.EASY` por defecto en lugar de lanzar una excepción fatal.
2. **Escape de caracteres en C++:** Validar que operadores y sintaxis C++ (`#include`, `<<`, `>>`, `&&`, `"\""`) no rompan el parser JSON.
3. **Selección múltiple:** Preguntas que contengan múltiples respuestas válidas deben tener `isCorrect: true` en todas ellas.
4. **Desglose de dificultad en BottomSheet:** Si un nivel no tiene retos de alguna dificultad, no debe mostrar conteos en 0 que ensucien la vista.
5. **Re-emisión de Room:** La adición del campo `difficulty` en Room y en la base de datos `codequiz.db` no debe reiniciar estados de sesión activos.

---

## Tareas de Implementación

### Tarea 1: Modelo de Dominio, Persistencia Room y DTOs para `ExerciseDifficulty`
**Archivos:**
- Crear: `app/src/main/java/com/code/Quiz/feature/quiz/domain/model/ExerciseDifficulty.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/quiz/domain/model/Exercise.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/quiz/data/entity/ExerciseEntity.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/course/data/dto/SeedDataDto.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/course/data/seeder/DatabaseSeeder.kt`
- Test: `app/src/test/java/com/code/Quiz/feature/quiz/data/entity/ExerciseEntityMapperTest.kt`

**Interfaces:**
- `ExerciseDifficulty`: enum con valores `EASY`, `MEDIUM`, `HARD`.
- `Exercise.difficulty: ExerciseDifficulty = ExerciseDifficulty.EASY`
- `ExerciseEntity.difficulty: String = "EASY"`
- `ExerciseDto.difficulty: String = "EASY"`

- [x] **Paso 1: Escribir test unitario de mapeo para `ExerciseEntity` y `Exercise` con `difficulty`**
  Verificar que `toDomain()` mapee `"EASY"` a `ExerciseDifficulty.EASY`, `"MEDIUM"` a `ExerciseDifficulty.MEDIUM`, `"HARD"` a `ExerciseDifficulty.HARD`, y valores desconocidos a `ExerciseDifficulty.EASY`.
- [x] **Paso 2: Crear el enum `ExerciseDifficulty.kt` en dominio**
  ```kotlin
  package com.code.Quiz.feature.quiz.domain.model

  enum class ExerciseDifficulty {
      EASY,
      MEDIUM,
      HARD
  }
  ```
- [x] **Paso 3: Actualizar `Exercise.kt`, `ExerciseEntity.kt`, `SeedDataDto.kt` y `DatabaseSeeder.kt`**
  Agregar el campo `difficulty` con valor por defecto `ExerciseDifficulty.EASY` / `"EASY"`.
- [x] **Paso 4: Ejecutar test de mapeo y verificar que pase**
  Ejecutar: `./gradlew testDebugUnitTest --tests "*.Exercise*MapperTest"`
- [x] **Paso 5: Commit**
  ```bash
  git add app/src/main/java/com/code/Quiz/feature/quiz/ app/src/main/java/com/code/Quiz/feature/course/data/
  git commit -m "feat(quiz): add ExerciseDifficulty domain enum, entity field, and DTO support"
  ```

---

### Tarea 2: Componente `DifficultyBadge` y Presentación en `QuizScreen` y `LevelDetailBottomSheet`
**Archivos:**
- Modificar: `app/src/main/java/com/code/Quiz/core/ui/component/badge/CodeQuizBadge.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizScreen.kt`
- Modificar: `app/src/main/java/com/code/Quiz/feature/course/ui/components/LevelDetailBottomSheet.kt`

**Interfaces:**
- `@Composable fun DifficultyBadge(difficulty: ExerciseDifficulty, modifier: Modifier = Modifier)`
- Muestra:
  - `EASY`: Texto `"FÁCIL"`, color `SuccessMint`, fondo `DarkSurfaceElevated`, borde sutil `SuccessMint.copy(alpha = 0.4f)`.
  - `MEDIUM`: Texto `"MEDIO"`, color `StreakOrange`, fondo `DarkSurfaceElevated`, borde sutil `StreakOrange.copy(alpha = 0.4f)`.
  - `HARD`: Texto `"DIFÍCIL"`, color `AccentPurple`, fondo `DarkSurfaceElevated`, borde sutil `AccentPurple.copy(alpha = 0.4f)`.

- [x] **Paso 1: Implementar `DifficultyBadge` en `CodeQuizBadge.kt` con `@Preview` para cada variante**
  Definir la función composable con soporte para las tres variantes de dificultad.
- [x] **Paso 2: Integrar `DifficultyBadge` en `QuizScreen.kt`**
  En el estado `QuizUiState.Content`, arriba del enunciado de la pregunta `exercise.question`, mostrar una fila con:
  - `DifficultyBadge(difficulty = exercise.difficulty)`
  - Spacer / punto separador `•`
  - Idioma: `"C++"` o `"Java"`
- [x] **Paso 3: Integrar el desglose de dificultad en `LevelDetailBottomSheet.kt`**
  Añadir un componente informativo que indique la distribución de dificultad estimada del nivel seleccionado.
- [x] **Paso 4: Verificar visualmente los previews de Compose**
  Compilar previews de `QuizScreen` y `LevelDetailBottomSheet`.
- [x] **Paso 5: Commit**
  ```bash
  git add app/src/main/java/com/code/Quiz/core/ui/component/badge/CodeQuizBadge.kt \
          app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizScreen.kt \
          app/src/main/java/com/code/Quiz/feature/course/ui/components/LevelDetailBottomSheet.kt
  git commit -m "feat(ui): implement DifficultyBadge and integrate into QuizScreen and LevelDetailBottomSheet"
  ```

---

### Tarea 3: Script de Extracción de `apk.txt` con Detección Automática de Dificultad
**Archivos:**
- Crear: `scripts/parse_apk_exercises.py`
- Test: `scripts/test_parse_apk_exercises.py`

**Interfaces:**
- Consume: `/Users/Andrew/Downloads/apk.txt`
- Mapeo de dificultad:
  - Encabezado `Nivel 1. Fácil` o `Nivel fácil` ➔ `"difficulty": "EASY"`
  - Encabezado `Nivel 2. Intermedio` o `Nivel medio` ➔ `"difficulty": "MEDIUM"`
  - Encabezado `Nivel 3. Difícil` o `Nivel difícil` ➔ `"difficulty": "HARD"`
- Produce: `app/build/extracted_cpp_exercises.json`

- [x] **Paso 1: Escribir test del parser verificando asignación de dificultad**
  Asegurar que los ejercicios 1 a 7 de Variables se etiqueten como `EASY`, del 8 al 14 como `MEDIUM`, y el 15 como `HARD`.
- [x] **Paso 2: Implementar `scripts/parse_apk_exercises.py`**
  Implementar la extracción de preguntas, opciones, snippets de C++, retroalimentación y bandera `difficulty`.
- [x] **Paso 3: Ejecutar extracción sobre `apk.txt`**
  Ejecutar: `python3 scripts/parse_apk_exercises.py --input /Users/Andrew/Downloads/apk.txt --output app/build/extracted_cpp_exercises.json`
  Verificar que los ~80 reactivos tengan su atributo `"difficulty"` correcto.
- [x] **Paso 4: Commit**
  ```bash
  git add scripts/parse_apk_exercises.py scripts/test_parse_apk_exercises.py
  git commit -m "feat(scripts): implement apk.txt parser with difficulty tagging"
  ```

---

### Tarea 4: Fusión en `seed_data.json` (Versión 7) y Ordenamiento Progresivo
**Archivos:**
- Modificar: `app/src/main/assets/data/seed_data.json`
- Crear: `scripts/merge_seed_data.py`

**Estrategia de Fusión:**
1. Los 30 ejercicios preexistentes de C++ se etiquetan con dificultad (ej. preguntas 1-2 `EASY`, 3-4 `MEDIUM`, 5 `HARD`).
2. Se fusionan con los 80 reactivos de `apk.txt` en sus 6 niveles respectivos.
3. Se ordenan los ejercicios dentro de cada nivel de manera ascendente: **`EASY` ➔ `MEDIUM` ➔ `HARD`**.
4. Se actualizan los campos `totalExercises` y `xpReward` de cada nivel.
5. Se actualiza `"version": 7`.

- [x] **Paso 1: Implementar `scripts/merge_seed_data.py`**
- [x] **Paso 2: Ejecutar fusión sobre `seed_data.json`**
  Ejecutar: `python3 scripts/merge_seed_data.py`
- [x] **Paso 3: Validar integridad del archivo `seed_data.json`**
  Verificar que JSON sea válido, versión sea 7, y conteo total de ejercicios sea > 140.
- [x] **Paso 4: Commit**
  ```bash
  git add app/src/main/assets/data/seed_data.json scripts/merge_seed_data.py
  git commit -m "feat(catalog): merge C++ exercises with progressive difficulty ordering (catalog v7)"
  ```

---

### Tarea 5: Verificación de Pruebas Unitarias de Android
**Archivos:**
- Modificar: `app/src/test/java/com/code/Quiz/feature/course/data/seeder/DatabaseSeederJsonTest.kt`
- Modificar: `app/src/test/java/com/code/Quiz/feature/course/data/seeder/DatabaseSeederSmartSyncTest.kt`

- [x] **Paso 1: Actualizar tests de seeder para verificar catálogo v7 y campo `difficulty`**
  Verificar que cada ejercicio en la base de datos tenga `difficulty` válido (`EASY`, `MEDIUM` o `HARD`).
- [x] **Paso 2: Ejecutar la suite completa de pruebas unitarias**
  Ejecutar: `./gradlew testDebugUnitTest`
  Resultado esperado: `BUILD SUCCESSFUL` con 100% de tests aprobados.
- [x] **Paso 3: Commit**
  ```bash
  git add app/src/test/java/com/code/Quiz/
  git commit -m "test(catalog): verify catalog v7 seeder and difficulty persistence"
  ```

---

### Tarea 6: Actualización Documental
**Archivos:**
- Modificar: `PROJECT.md`
- Modificar: `AUDIT.md`

- [x] **Paso 1: Actualizar `PROJECT.md`**
  - Documentar el sistema de dificultades (`Fácil`, `Medio`, `Difícil`) en la sección de arquitectura pedagógica.
  - Actualizar el inventario de ejercicios y módulos C++.
- [x] **Paso 2: Actualizar `AUDIT.md`**
  - Registrar la migración a catálogo v7 y la resolución de la integración de contenidos.
- [x] **Paso 3: Commit final**
  ```bash
  git add PROJECT.md AUDIT.md
  git commit -m "docs: document difficulty indicator system and expanded C++ curriculum"
  ```

---

## Verificación Final
- La app muestra badges visuales claros (`[ 🟢 FÁCIL ]`, `[ 🟡 MEDIO ]`, `[ 🟣 DIFÍCIL ]`) durante la resolución de retos.
- El usuario experimenta una curva de dificultad suave en cada lección.
- Cero pérdida de ejercicios anteriores.
- 100% de tests unitarios pasando.
