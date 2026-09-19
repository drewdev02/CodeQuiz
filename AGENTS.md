# AGENTS.md — Development Guidelines & Architectural Standards

This document establishes the mandatory architectural patterns, engineering conventions, and coding standards for all AI agents and contributors working on the **CodeQuiz** project.

---

## 1. Core Architectural Principles

### 1.1 Package-by-Feature (Modular Monolith / Vertical Slices)
The project is built inside a **single Gradle module (`:app`)**, but strictly organized as a **Modular Monolith (Package-by-Feature)**. 

Instead of traditional horizontal layers at the root (`/data`, `/domain`, `/ui`), the codebase is divided vertically by business capabilities (features). Each feature behaves as an autonomous, self-contained "logical module" with its own Clean Architecture layers:

```
┌────────────────────────────────────────────────────────────────────────┐
│                              :app module                               │
│                                                                        │
│  ┌───────────────────────┐             ┌────────────────────────────┐  │
│  │     feature/quiz      │             │       feature/streak       │  │
│  │ ┌───────────────────┐ │             │ ┌────────────────────────┐ │  │
│  │ │ ui (MVVM/Compose) │ │             │ │ ui (MVVM/Compose)      │ │  │
│  │ └─────────┬─────────┘ │             │ └───────────┬────────────┘ │  │
│  │           ▼           │             │             ▼              │  │
│  │ ┌───────────────────┐ │             │ ┌────────────────────────┐ │  │
│  │ │ domain (Pure Kt)  │ │             │ │ domain (Pure Kt)       │ │  │
│  │ └─────────▲─────────┘ │             │ └───────────▲────────────┘ │  │
│  │           │           │             │             │              │  │
│  │ ┌─────────┴─────────┐ │             │ ┌───────────┴────────────┐ │  │
│  │ │ data (Room/DAOs)  │ │             │ │ data (Room/DAOs)       │ │  │
│  │ └───────────────────┘ │             │ └────────────────────────┘ │  │
│  │ di (Koin module)      │             │ di (Koin module)           │  │
│  └───────────────────────┘             └────────────────────────────┘  │
│                              ▲       ▲                                 │
│                              │       │                                 │
│                      ┌───────┴───────┴────────┐                        │
│                      │          core/         │                        │
│                      │  database, theme, util │                        │
│                      └────────────────────────┘                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Clean Architecture Inside Each Feature
Every feature follows strict layer boundaries:

1. **`feature/<name>/domain/` (Domain Layer)**:
   - **Pure Kotlin (`no android.*` imports)**.
   - Contains feature business entities, Use Cases / Interactors, and **Repository Interfaces** (abstractions).
   - Never depends on Room, Koin, or presentation classes.
2. **`feature/<name>/data/` (Data Layer)**:
   - Implements the feature's repository interfaces.
   - Defines feature-specific Room DAOs, Entities, and explicit mapper extension functions (`toDomain()`, `toEntity()`).
   - Executes database operations on `Dispatchers.IO`.
3. **`feature/<name>/ui/` (Presentation Layer)**:
   - Implements **MVVM** using Jetpack Compose.
   - Exposes an immutable `StateFlow<UiState>` following **Unidirectional Data Flow (UDF)**.
   - Depends only on the feature's Domain Use Cases and Repository Interfaces.
4. **`feature/<name>/di/` (Feature DI Module)**:
   - Contains the Koin module defining the dependencies for this feature.

### 1.3 Shared & Cross-Cutting Concerns: `core/`
Cross-cutting infrastructure lives in `core/`:
- `core/database/`: Global `RoomDatabase` instance, type converters, and migration schemas.
- `core/theme/`: Jetpack Compose Material 3 theme, colors, typography, shapes.
- `core/util/`: Common extensions, dispatcher providers, and reusable utility helpers.

---

## 2. Cross-Feature Communication Rules

To maintain high cohesion and low coupling:

1. **No Direct UI/Data Coupling**: A feature must **never** directly import or reference classes from another feature's `ui` or `data` packages.
2. **Communication via Domain Abstractions**: If `feature/quiz` needs data or actions from `feature/streak`, it must communicate through public domain models or shared interfaces defined in `core/` or exported via domain contracts.
3. **Navigation Independence**: Navigation between features is coordinated at the app root level (`ui/navigation/`) using route destinations and arguments, never by direct tight coupling between screens.
4. **Future Multi-Module Readiness**: Because each feature is self-contained with its own `domain`, `data`, `ui`, and `di`, any feature can be extracted into an independent Gradle module (e.g., `:feature:quiz`) in the future with zero refactoring.

---

## 3. Dependency Injection: Koin

The application standardizes on **Koin** for dependency injection.

### 3.1 Constructor Injection is Mandatory
- **Every class must receive its dependencies via constructor parameters**.
- Never use field injection (`lateinit var` with injection).
- Never use the Service Locator anti-pattern (do not call `get()` or `KoinPlatformTools` inside domain or data classes).
- ViewModels are injected into composables via `koinViewModel()`.

### 3.2 Modular Koin Configuration
Each feature encapsulates its own Koin definitions:

```kotlin
// feature/quiz/di/QuizModule.kt
val quizModule = module {
    single { get<CodeQuizDatabase>().quizDao() }
    single<QuizRepository> { QuizRepositoryImpl(quizDao = get()) }
    factory { EvaluateAnswerUseCase(quizRepository = get()) }
    viewModelOf(::QuizViewModel)
}
```

Root Koin aggregation:
```kotlin
// di/AppModule.kt
val appModules = listOf(
    coreModule,
    quizModule,
    streakModule,
    profileModule
)
```

---

## 4. Local Persistence: Room Database

Local persistence is managed exclusively through **Android Jetpack Room**.

### 4.1 Rules for Room Persistence
1. **Entity Separation**:
   - Room tables are defined in `feature/<name>/data/entity/` and annotated with `@Entity`.
   - Never use Room entities in the Domain or Presentation layers.
   - Always write bidirectional mapper extension functions:
     ```kotlin
     fun QuestionEntity.toDomain(): Question
     fun Question.toEntity(): QuestionEntity
     ```
2. **Reactive Reads & Suspend Writes**:
   - Read queries observing data changes must return Kotlin `Flow<T>`.
   - One-shot write/update/delete operations must be marked `suspend`.
   - DAOs and repositories must execute on `Dispatchers.IO`.
3. **Centralized Database**:
   - The central `CodeQuizDatabase` class lives in `core/database/` and registers all feature DAOs.

---

## 5. Presentation & MVVM Standards

### 5.1 Unidirectional Data Flow (UDF)
- **State**: The ViewModel exposes a single, immutable `StateFlow<UiState>`.
- **Events**: The UI communicates user actions to the ViewModel via a sealed interface of events (e.g., `QuizUiEvent.OptionSelected`, `QuizUiEvent.SubmitAnswer`).
- **One-off Effects**: Transient events (navigation, snackbars) use `Channel<UiEffect>` or `SharedFlow`.

### 5.2 State Modeling with Sealed Interfaces
Always model UI states comprehensively using sealed interfaces:

```kotlin
sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Content(
        val question: Question,
        val selectedOptionIds: Set<String>,
        val isSubmitted: Boolean,
        val feedback: Feedback? = null
    ) : QuizUiState
    data class Error(val message: String) : QuizUiState
}
```

### 5.3 Jetpack Compose Guidelines
- Make composables stateless whenever possible by hoisting state and passing lambda callbacks.
- Compose previews must be provided for all major UI components and states.
- Follow Material 3 theming; do not hardcode colors, padding, or fonts directly in composables.

---

## 6. Idiomatic Kotlin Standards

All code must strictly adhere to modern, idiomatic Kotlin conventions:

### 6.1 Immutability by Default
- Always prefer `val` over `var`.
- Use read-only collections (`List`, `Set`, `Map`) in public APIs and domain models. Keep `MutableList` strictly scoped and private when necessary.
- Data classes should contain only `val` properties.

### 6.2 Sealed Hierarchies & Pattern Matching
- Use `sealed interface` for closed type hierarchies (UI states, UI events, domain results, question types).
- Use exhaustive `when` expressions without unnecessary `else` branches to ensure compiler safety when new variants are added.

### 6.3 Null Safety
- **Never use `!!` (double bang)** under any circumstance.
- Utilize safe calls (`?.`), the Elvis operator (`?:`), `checkNotNull()`, or `requireNotNull()` with informative error messages.

### 6.4 Functional & Scope Functions
- Use Kotlin standard library functions idiomatically (`filter`, `map`, `associateBy`, `groupBy`).
- Use scope functions (`let`, `apply`, `also`, `run`) with clear intent:
  - `apply` for object initialization/configuration.
  - `let` for non-null checks and scoping transformations.
  - `also` for side effects (e.g., logging).

### 6.5 Coroutines & Structured Concurrency
- Never use `GlobalScope`.
- Use `viewModelScope` in ViewModels and inject coroutine dispatchers (`CoroutineDispatcher`) to enable clean unit testing.

---

## 7. Project Directory & Package Structure

```
com.code.Quiz/
├── core/                                # Shared infrastructure & cross-cutting code
│   ├── database/                        # Central RoomDatabase & TypeConverters
│   │   ├── CodeQuizDatabase.kt
│   │   └── Converters.kt
│   ├── theme/                           # Material 3 Theme, Color, Type
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── util/                            # Shared utilities and dispatchers
│       └── DispatcherProvider.kt
│
├── feature/                             # Vertical slices (Package-by-Feature)
│   ├── quiz/                            # Quiz Engine Feature
│   │   ├── data/
│   │   │   ├── dao/QuizDao.kt
│   │   │   ├── entity/QuestionEntity.kt
│   │   │   ├── mapper/QuestionMapper.kt
│   │   │   └── repository/QuizRepositoryImpl.kt
│   │   ├── di/
│   │   │   └── QuizModule.kt
│   │   ├── domain/
│   │   │   ├── model/Question.kt
│   │   │   ├── model/Option.kt
│   │   │   ├── repository/QuizRepository.kt
│   │   │   └── usecase/EvaluateAnswerUseCase.kt
│   │   └── ui/
│   │       ├── components/CodeSnippetCard.kt
│   │       ├── QuizScreen.kt
│   │       ├── QuizViewModel.kt
│   │       └── QuizUiState.kt
│   │
│   ├── streak/                          # Daily Streak & Gamification Feature
│   │   ├── data/
│   │   │   ├── dao/StreakDao.kt
│   │   │   ├── entity/StreakEntity.kt
│   │   │   └── repository/StreakRepositoryImpl.kt
│   │   ├── di/
│   │   │   └── StreakModule.kt
│   │   ├── domain/
│   │   │   ├── model/Streak.kt
│   │   │   ├── repository/StreakRepository.kt
│   │   │   └── usecase/UpdateStreakUseCase.kt
│   │   └── ui/
│   │       ├── components/StreakBadge.kt
│   │       └── StreakBanner.kt
│   │
│   └── profile/                         # User Profile & Stats Feature
│       ├── data/
│       ├── di/
│       ├── domain/
│       └── ui/
│
├── di/                                  # Central Koin setup
│   ├── CoreModule.kt                    # Core dependencies (Database, Dispatchers)
│   └── AppModule.kt                     # Aggregates all feature modules
│
├── ui/
│   └── navigation/                      # App-level navigation graph
│       └── AppNavGraph.kt
│
├── MainActivity.kt                      # Main Activity hosting AppNavGraph
└── CodeQuizApplication.kt               # Initializes Koin with appModules
```

---

## 8. Quality Checklist for Agent PRs & Code Changes

Before completing any task or code modification, agents must verify:
- [ ] Is the new code placed inside its corresponding `feature/<name>/` package?
- [ ] Does each feature encapsulate its own `domain`, `data`, `ui`, and `di` packages?
- [ ] Are cross-feature dependencies decoupled (no direct imports of another feature's `data` or `ui`)?
- [ ] Are all dependencies injected via constructors?
- [ ] Is the domain layer free of Android/framework imports?
- [ ] Are repository contracts defined in `domain` and implemented in `data`?
- [ ] Are Room entities converted to Domain models before leaving the Data layer?
- [ ] Is the Kotlin code idiomatic (no `!!`, immutable `val`, sealed interfaces for states)?
- [ ] Is the UI reactive using `StateFlow` and Jetpack Compose?
