# AUDIT.md — Reporte de Auditoría Técnica y Hoja de Ruta

> **Proyecto:** CodeQuiz (Plataforma Gamificada de Aprendizaje de Programación para Android)  
> **Fecha de auditoría:** Octubre 2026  
> **Estado de compilación y pruebas:** 77 pruebas unitarias automatizadas ejecutadas y aprobadas (100% éxito, 0 fallos).  
> **Catálogo de Contenidos:** Versión 7 con 178 micro-retos interactivos (148 en C++ y 30 en Java) y sistema de dificultad pedagógica (`FÁCIL`, `MEDIO`, `DIFÍCIL`).  
> **Documentos de referencia:** [`AGENTS.md`](./AGENTS.md), [`PROJECT.md`](./PROJECT.md), [`DESIGN.md`](./DESIGN.md).

---

## 1. Resumen Ejecutivo

CodeQuiz cuenta con una base de código moderna y de alta calidad construida sobre Kotlin 2.2, Jetpack Compose (Material 3), Room Database y Koin. La aplicación implementa un Monolito Modular (*Package-by-Feature*), flujo de datos unidireccional (UDF) y un sistema de resaltado sintáctico de código propio adaptado a pantallas táctiles móviles.

No obstante, la auditoría ha detectado una serie de **puntos de acoplamiento directo entre capas de datos de diferentes características**, inyecciones de casos de uso directamente en composables y riesgos potenciales de re-emisión en flujos de Room que deben abordarse antes de extraer módulos Gradle independientes.

---

## 2. Matriz de Cumplimiento con `AGENTS.md`

| Área / Directriz | Estado | Observación |
| :--- | :---: | :--- |
| **Monolito Modular (Package-by-Feature)** | ⚠️ Parcial | Estructura vertical correcta, pero existen importaciones directas de DAOs entre features. |
| **Clean Architecture (Domain Puro)** | ✅ Cumple | La capa de dominio está 100% libre de imports `android.*` y frameworks. |
| **Inyección de Dependencias (Koin)** | ⚠️ Parcial | Correcto en Repositorios y ViewModels; uso inadecuado de `koinInject()` en 3 pantallas Compose. |
| **Persistencia Local (Room & DataStore)** | ✅ Cumple | Mappers explícitos `toDomain()` / `toEntity()`, transacciones atómicas con `withTransaction`. |
| **Patrón Presentación (MVVM & UDF)** | ✅ Cumple | `StateFlow<UiState>` inmutable derivado de eventos y jerarquías de estados selladas. |
| **Sistema de Diseño Central (`core/ui/component`)** | ⚠️ Parcial | Muy buena adopción general; excepciones con `Surface` crudo y dp mágicos en Profile. |
| **Kotlin Idiomático & Null Safety** | ✅ Cumple | Cero uso de `!!`, inmutabilidad con `val`, jerarquías `sealed interface`. |
| **Suite de Pruebas Unitarias** | ✅ Sobresaliente | 75 unit tests ejecutados y pasando en < 1 segundo. |

---

## 3. Hallazgos Técnicos y Deuda Arquitectónica Identificada

```mermaid
graph TD
    subgraph Violacion1 ["1. Acoplamiento Cross-Feature (Data)"]
        QuizRepo["QuizRepositoryImpl"] -->|Importa| CourseDao["CourseLevelDao (feature:course)"]
        ProfileRepo["ProfileRepositoryImpl"] -->|Importa| CourseDao
        ProfileRepo -->|Importa| StreakDao["StreakDao (feature:streak)"]
    end

    subgraph Violacion2 ["2. Ubicación de Seeder"]
        Seeder["DatabaseSeeder (feature:course)"] -->|Importa| AllDAOs["DAOs de Library, Quiz y Streak"]
    end

    subgraph Violacion3 ["3. Inyección en UI"]
        Screens["Screens Compose"] -->|koinInject()| GetStreakUseCase["GetStreakUseCase"]
    end
```

### 3.1 Acoplamiento Directo Cross-Feature en Capa de Datos (Alta Prioridad)
* **Regla violada:** `AGENTS.md` §1.1 y §2.1 (*"No Direct UI/Data Coupling: A feature must never directly import or reference classes from another feature's ui or data packages"*).
* **Archivos afectados:**
  - `feature/quiz/data/repository/QuizRepositoryImpl.kt`: Importa directamente `CourseLevelDao` y `DatabaseSeeder` desde `feature.course.data`.
  - `feature/profile/data/repository/ProfileRepositoryImpl.kt`: Importa directamente `CourseLevelDao` (`feature.course.data`) y `StreakDao` (`feature.streak.data`).
* **Consecuencia:** Rompe la autonomía de las features e impide modularizar la aplicación en submódulos de Gradle independientes (`:feature:quiz`, `:feature:course`).
* **Solución requerida:** La comunicación debe realizarse a través de las abstracciones de repositorio en dominio (`CourseRepository`, `StreakRepository`) o mediante contratos/interactors en `core/`.

---

### 3.2 Ubicación Anómala de `DatabaseSeeder` (Alta Prioridad)
* **Problema:** `DatabaseSeeder.kt` reside en `feature/course/data/seeder/`, pero orquesta el llenado y sincronización de entidades de toda la base de datos (`CourseDao`, `CourseLevelDao`, `ExerciseDao`, `ArticleDao`, `StreakDao`).
* **Consecuencia:** Convierte a la característica de cursos en un punto de acoplamiento central para toda la aplicación.
* **Solución requerida:** Mover `DatabaseSeeder.kt` a `core/database/seeder/` (o al módulo central de base de datos) y registrarlo dentro de `CoreModule.kt`.

---

### 3.3 Inyección Directa en Composables (`koinInject()` vs MVVM) (Media Prioridad)
* **Regla violada:** `AGENTS.md` §3.1 (*"ViewModels are injected into composables via koinViewModel(). Constructor injection is mandatory"*).
* **Archivos afectados:**
  - `feature/course/ui/CoursePathScreen.kt`: `getStreakUseCase: GetStreakUseCase = koinInject()`
  - `feature/course/ui/CourseLevelsScreen.kt`: `getStreakUseCase: GetStreakUseCase = koinInject()`
  - `feature/library/ui/LibraryScreen.kt`: `getStreakUseCase: GetStreakUseCase = koinInject()`
* **Consecuencia:** La vista consume directamente la lógica de negocio saltándose el ViewModel y rompiendo el flujo unidireccional de datos (UDF).
* **Solución requerida:** Incorporar el estado de la racha (`streakCount`) dentro del `UiState` de cada ViewModel respectivo (`CourseUiState`, `CourseLevelsUiState`, `LibraryUiState`), o manejarlo a nivel de Scaffold de navegación superior.

---

### 3.4 Riesgo de Reseteo de Sesión de Quiz en `QuizViewModel` (Media Prioridad)
* **Archivo afectado:** `feature/quiz/ui/QuizViewModel.kt` (método `loadLevel`).
* **Problema:** Se consume el `Flow<List<Exercise>>` directamente con `.collect { ... }` reseteando el estado a `currentIndex = 0` y `score = 0`. Si Room emite una actualización de tabla (por sincronización en segundo plano o inserciones concurrentes), el usuario perderá su posición en el quiz.
* **Solución requerida:** Utilizar `.first()` para congelar la lista de ejercicios durante la sesión activa del nivel, o condicionar la asignación comprobando si el estado actual ya es `QuizUiState.Content`.

---

### 3.5 Corrutina No Supervisada en Inicialización de la App (Media Prioridad)
* **Archivo afectado:** `CodeQuizApplication.kt` (líneas 24–26).
* **Problema:** Se lanza `CoroutineScope(Dispatchers.IO).launch { seeder.seedIfNeeded() }` sin un `CoroutineExceptionHandler`. Cualquier fallo inesperado de deserialización o I/O en arranque podría provocar cierres no controlados.
* **Solución requerida:** Configurar un `CoroutineExceptionHandler` con logging apropiado o gestionar la sincronización mediante `WorkManager` / `AppNavViewModel`.

---

### 3.6 Desviaciones del Sistema de Diseño (`AGENTS.md` §5.4) (Baja Prioridad)
* **Archivos afectados:**
  - `feature/profile/ui/components/DuolingoStreakCard.kt`:
    - Uso de primitivos `Surface` de Material 3 en lugar de `CodeQuizCard` o `Box` con modificadores.
    - Valores mágicos de espaciado en `dp` (`36.dp`, `32.dp`, `30.dp`, `8.dp`, `16.dp`) en vez de los tokens `Spacing.*`.
  - `feature/quiz/ui/QuizScreen.kt`:
    - Uso de `Surface` directo en las barras inferiores de comprobación y feedback.
* **Solución requerida:** Sustituir por wrappers de diseño de `core/ui/component/*` y tokens de espaciado estandarizados.

---

### 3.7 Código Muerto en Navegación (Baja Prioridad)
* **Archivo afectado:** `navigation/AppNavGraph.kt` (líneas 200–381).
* **Problema:** Contiene las definiciones completas de `HomeScreen` y `QuizPlaceholderScreen` con sus previews, las cuales ya no son utilizadas por el flujo real de rutas.
* **Solución requerida:** Depurar y eliminar estas funciones huérfanas para reducir el peso y complejidad del archivo.

---

## 4. Hoja de Ruta de Refactorización (Checklist de Ejecución)

### Fase 1: Reorganización Arquitectónica y Desacoplamiento (Prioridad Alta)
- [ ] Mover `DatabaseSeeder.kt` de `feature/course/data/seeder/` a `core/database/seeder/`.
- [ ] Ajustar `CoreModule.kt` y `CourseModule.kt` para la nueva ubicación del seeder.
- [ ] Refactorizar `QuizRepositoryImpl.kt` para que interactúe con la actualización de niveles a través de `CourseRepository` (o un Use Case dedicado) en vez de `CourseLevelDao`.
- [ ] Refactorizar `ProfileRepositoryImpl.kt` para obtener estadísticas mediante interfaces de dominio (`CourseRepository`, `StreakRepository`) eliminando dependencias de DAOs cruzados.

### Fase 2: Robustez de Flujos y Presentación (Prioridad Media)
- [ ] Eliminar `koinInject()` en `CoursePathScreen.kt`, `CourseLevelsScreen.kt` y `LibraryScreen.kt`; integrar `streak` en sus respectivos ViewModels y estados de interfaz.
- [ ] Blindar `QuizViewModel.loadLevel()` para que no reinicie el quiz ante emisiones secundarias de Room.
- [ ] Agregar manejo de excepciones y ciclo de vida controlado a la llamada inicial del seeder en `CodeQuizApplication.kt`.

### Fase 3: Limpieza y Ajustes de Diseño (Prioridad Baja)
- [ ] Eliminar `HomeScreen` y `QuizPlaceholderScreen` de `AppNavGraph.kt`.
- [ ] Reemplazar instancias de `Surface` y números mágicos en `DuolingoStreakCard.kt` por componentes `CodeQuizCard` y tokens `Spacing.*`.
- [ ] Reemplazar instancias de `Surface` en `QuizScreen.kt` por contenedores estandarizados.
- [x] Ejecutar la suite completa de pruebas unitarias (`./gradlew testDebugUnitTest`) para validar no regresión (77/77 pruebas pasando).

---

## 5. Actualización Curricular y Microaprendizaje (Catálogo v8)

En cumplimiento estricto con la metodología pedagógica de **microaprendizaje** (sesiones de 3 a 5 minutos) y las directrices del usuario:

1. **Integración Completa de Ejercicios:**
   - Se integraron 118 ejercicios de programación desde `apk.txt`, combinándolos con los 60 ejercicios existentes sin pérdidas (178 retos totales: 148 de C++ y 30 de Java).
2. **Sistema de Dificultad Pedagógica:**
   - Incorporación del enum `ExerciseDifficulty` (`EASY`, `MEDIUM`, `HARD`) con insignias visuales oficiales (`SuccessMint`, `StreakOrange`, `AccentPurple`) y curva pedagógica interactiva.
3. **Micro-Niveles (Máximo 5 Ejercicios):**
   - El curso de C++ fue re-particionado de 6 niveles extensos (20-32 ejercicios) a **33 micro-niveles pedagógicos** con un máximo estricto de 5 ejercicios por nivel.
   - En conjunto con los 6 niveles de Java (5 ejercicios cada uno), la app ofrece **39 niveles** ágiles y progresivos.
   - Sincronización inteligente de catálogo implementada en Room (Catálogo v8, Base de datos Room v5).

---

## 6. Mejoras de Accesibilidad y Adaptabilidad de Pantallas (WCAG & Material Design)

En cumplimiento con las directrices oficiales de accesibilidad de Google y Android (escalado de fuentes hasta 200% y dispositivos compactos $\le 360\text{ dp}$):

1. **Barra de Navegación Inferior ([`CodeQuizBottomBar.kt`](app/src/main/java/com/code/Quiz/core/ui/component/navigation/CodeQuizBottomBar.kt)):**
   - Migración de disposición horizontal rígida a disposición vertical estándar de Material 3 Navigation Bar (Icono centrado con píldora activa + Texto con `maxLines = 1` y `TextOverflow.Ellipsis`).
   - Se eliminó el truncado y fragmentación vertical de palabras como `"Biblioteca"`.
2. **Curva Pedagógica y Métricas ([`LevelDetailBottomSheet.kt`](app/src/main/java/com/code/Quiz/feature/course/ui/components/LevelDetailBottomSheet.kt)):**
   - La curva de dificultad ahora se apila verticalmente con ancho completo para los badges, evitando que `DIFÍCIL` sea empujado fuera de la pantalla.
   - Las métricas (`Micro-retos`, `XP Recompensa`, `Tiempo estimado`) se equilibran mediante `weight(1f)`.
   - Se habilitó scroll vertical en el contenido del bottom sheet para pantallas pequeñas.
3. **Encabezados TopBar ([`CoursePathScreen.kt`](app/src/main/java/com/code/Quiz/feature/course/ui/CoursePathScreen.kt), [`ProgressScreen.kt`](app/src/main/java/com/code/Quiz/feature/profile/ui/ProgressScreen.kt), [`CourseLevelsScreen.kt`](app/src/main/java/com/code/Quiz/feature/course/ui/CourseLevelsScreen.kt)):**
   - Reemplazo de alineaciones fijas en `Box` por `Row` con `weight(1f)` y `TextOverflow.Ellipsis`, previniendo colisiones contra el `StreakBadge`.
   - Soporte para 2 líneas en títulos largos de cursos.
4. **Retroalimentación en Quiz ([`QuizScreen.kt`](app/src/main/java/com/code/Quiz/feature/quiz/ui/QuizScreen.kt)):**
   - Delimitación con `heightIn(max = 140.dp)` y scroll vertical en el área de explicación del `bottomBar`, garantizando que el botón de avance nunca quede fuera del viewport.
5. **Tarjeta de Racha ([`DuolingoStreakCard.kt`](app/src/main/java/com/code/Quiz/feature/profile/ui/components/DuolingoStreakCard.kt)):**
   - Peso flexible `Modifier.weight(1f, fill = false)` en la columna de texto del encabezado para evitar desbordes en pantallas compactas.


