# CodeQuiz — Interactive Programming Learning Platform

> **Academic Project — Master's Degree (Maestría)**  
> An interactive, gamified mobile learning platform designed for mastering programming concepts through active practice, inspired by Duolingo's microlearning methodology (no passive video lectures).

---

## 1. Project Overview

### 1.1 Vision & Problem Statement
Traditional online learning platforms often rely heavily on passive video lectures, leading to high drop-off rates and the "illusion of competence" where students feel they understand concepts until they attempt to write code independently.

**CodeQuiz** addresses this challenge by providing a **100% interactive, microlearning-driven mobile application** for Android. Inspired by Duolingo's gamification and pedagogy, the platform teaches programming logic, syntax, algorithms, and software engineering principles through short, engaging, hands-on micro-challenges directly on the phone.

### 1.2 Key Differentiators
- **Zero Passive Videos**: Every lesson consists of interactive challenges requiring active input, problem-solving, and code comprehension.
- **Microlearning Sessions**: Lessons are structured to be completed in 3 to 5 minutes, ideal for daily mobile practice.
- **Instant Explanations & Feedback**: Immediate validation for every answer, complete with clear explanations of why a solution is correct or incorrect.
- **Comprehensive Gamification**: Habit-building mechanics (streaks, hearts/lives, experience points, milestones) that boost retention and consistency.

---

## 2. Pedagogical Architecture

The application is built on established computer science education and cognitive psychology principles:

| Principle | Implementation in CodeQuiz |
| :--- | :--- |
| **Active Recall & Retrieval Practice** | Students retrieve concepts from memory instead of re-reading or watching videos. |
| **Parson’s Programming Problems** | Reconstructing scrambled code blocks to teach syntax and control flow without typing friction on mobile. |
| **Immediate Formative Feedback** | Instant feedback loops prevent misconceptions from cementing. |
| **Spaced Repetition** | Review modules dynamically re-introduce previously learned concepts before they are forgotten. |
| **Scaffolding** | New concepts start with guided identification and advance to code completion and output prediction. |

---

## 3. Challenge Format & Exercise Types

### 3.1 Core Format (Current Scope / MVP)
For the current phase of the project, exercises follow a clean, highly focused **Multiple-Choice Question Engine with Code Integration**:

- **Question Structure**:
  - **Prompt / Text**: Conceptual question or problem statement.
  - **Difficulty Indicator**: Explicit cognitive difficulty level (`FÁCIL`, `MEDIO`, `DIFÍCIL`) with dedicated visual tokens (`SuccessMint`, `StreakOrange`, `AccentPurple`).
  - **Code Snippet (Optional)**: Formatted code block for analysis, syntax review, or output prediction.
  - **Options**: $N$ possible answer choices (text or inline code tokens).
  - **Explanation**: Pedagogical rationale displayed immediately upon submission explaining why the correct answers are right and why distractors are wrong.
- **Cognitive Scaffolding & Progressive Difficulty Curve**:
  - Every lesson presents challenges in progressive order: **Fácil** (conceptual recognition) ➔ **Medio** (application, tracing, typing) ➔ **Difícil** (multi-concept integration, arrays/matrices, output prediction).
- **Selection Modes**:
  1. **Single-Choice (1 correct answer)**: Radio-style selection where only one option can be chosen.
  2. **Multi-Select (1 or more correct answers)**: Checkbox-style selection where the learner must select all applicable correct options to succeed.
- **Immediate Formative Feedback**:
  - Color-coded feedback (Success green / Error red).
  - Clear breakdown showing which options were correctly or incorrectly checked.
  - Detailed explanation card shown immediately after submitting.

### 3.2 Future Exercise Backlog (Post-MVP)
Advanced interactive challenge types deferred for future phases after validating the core quiz engine:
- *Parson's Problems* (drag-and-drop code line reordering)
- *Spot the Bug* (tap-to-identify error line)
- *Fill-in-the-Blank* (code token insertion)


---

## 4. Gamification & User Progression

To sustain long-term engagement and consistent study habits:

- **Daily Streaks (Rachas)**: Tracks consecutive days of practice with streak freezes and reminders.
- **Experience Points (XP)**: Earned per completed challenge and lesson to measure cumulative effort.
- **Hearts / Lives System**: Balances challenge and focus, encouraging users to review mistakes.
- **Skill Tree / Roadmap**: Visual learning path where nodes represent modules, unlocking sequentially.
- **Mastery Levels**: Each skill node contains multiple crowns/levels (e.g., Intro, Practice, Mastery).
- **Achievements & Badges**: Unlockable milestones for accomplishments (e.g., "7-Day Streak", "Bug Hunter", "Algorithm Master").

---

## 5. Technical Architecture & Tech Stack

The project is built as a native Android application utilizing modern Android development practices (MAD):

```
app/src/main/java/com/code/Quiz/
├── data/
│   ├── local/              # Room DB, DataStore preferences, JSON asset parsers
│   ├── model/              # DTOs, entity definitions, mock data
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Business entities (Lesson, Challenge, UserProgress, Streak)
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases (EvaluateAnswerUseCase, UpdateStreakUseCase, etc.)
├── ui/
│   ├── navigation/         # Compose Destinations / Navigation graph
│   ├── components/         # Reusable widgets (CodeBlock, HeartCounter, StreakBadge)
│   ├── screens/
│   │   ├── home/           # Skill Tree / Curriculum path screen
│   │   ├── lesson/         # Active interactive challenge screen
│   │   ├── result/         # Post-lesson summary & XP calculation
│   │   └── profile/        # User statistics, streak history, achievements
│   └── theme/              # Material 3 colors, typography, shapes
└── MainActivity.kt         # Single Activity hosting Jetpack Compose Navigation
```

### Technology Specifications
- **Operating System**: Android (Min SDK: 24 / Android 7.0+, Target/Compile SDK: 37)
- **Language**: Kotlin 2.2.10
- **UI Framework**: [Jetpack Compose](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/gradle/libs.versions.toml#L19-L26) with Material Design 3
- **Architecture**: Clean Architecture + MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
- **Asynchronous & State**: Kotlin Coroutines & `StateFlow`
- **Dependency Catalog**: Gradle Version Catalog ([`libs.versions.toml`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/gradle/libs.versions.toml))
- **Local Storage** *(planned)*: Room Database for offline lesson storage and progress persistence; DataStore for user preferences.

---

## 6. Academic Context & Master's Objectives

This application serves as a core artifact for a **Master's Degree (Maestría)** project. Its academic objectives include:

1. **Investigating the Effectiveness of Microlearning in Software Engineering**: Comparing learning velocity and concept retention between interactive micro-challenges and traditional video-centric courses.
2. **Mobile Pedagogical Design Patterns**: Establishing best UI/UX practices for code representation, manipulation, and debugging on touch screens without full physical keyboards.
3. **Evaluating Gamification Impact**: Measuring how streak mechanics and instant feedback loops influence study consistency among computer science students.

---

## 7. Roadmap

- [x] **Phase 0: Project Setup & Baseline**
  - Android Studio template configured with Jetpack Compose & Material 3.
  - Gradle version catalog setup.
- [x] **Phase 1: Core Domain Models & Challenge Engine**
  - Define challenge types, lesson data models, and answer evaluation logic.
  - Unit tests for scoring, answer validation, difficulty levels (`EASY`, `MEDIUM`, `HARD`), and streak calculations.
  - Bundled curriculum with 178 micro-challenges across 39 bite-sized levels (33 C++ Linear Programming micro-levels and 6 Java OOP Mastery levels, strictly <= 5 exercises per level honoring the 3-5 minute microlearning format).
- [x] **Phase 2: Interactive Compose Challenge Components**
  - Implement interactive UI composables: Code snippet viewers with native syntax highlighting, difficulty badges (`DifficultyBadge`), multiple-choice selectors.
  - Animated feedback bottom-sheet (correct/incorrect states, explanations, sound/haptics).
- [x] **Phase 3: Curriculum Navigation & Skill Tree**
  - Interactive path/map screen showing lesson nodes, progress status, difficulty breakdown, and unlock conditions.
- [x] **Phase 4: Gamification & Local Persistence**
  - Implement Room DB for offline-first progress tracking with smart catalog synchronization.
  - Streak tracking, XP system, and user stats screen with Duolingo streak calendar.
- [ ] **Phase 5: Academic Evaluation & Validation**
  - Pilot testing with student groups, usability benchmarks, and thesis evaluation metrics.

---

## 8. Future Architecture: Offline-First & Remote JSON Sync

*(Documented architectural proposal for post-MVP phase)*

To expand the platform with dynamically updated content while preserving zero-latency offline usage:

### 8.1 Single Source of Truth (SSOT) via Room
- The UI exclusively observes local Room database queries (`Flow<List<Question>>`).
- The application remains 100% functional offline without an active internet connection.

### 8.2 Pre-Packaged Seed Content
- A starter curriculum is bundled in `app/src/main/assets/seed_exercises.json`.
- On initial launch, if the database is unpopulated, Room pre-populates the database from this asset file, giving immediate access to lessons without requiring a network download.

### 8.3 Lightweight Manifest Versioning
- Sync checks query a minimal JSON manifest endpoint first:
  ```json
  {
    "contentVersion": 5,
    "lastUpdated": "2026-09-19T12:00:00Z",
    "downloadUrl": "https://.../exercises_v5.json"
  }
  ```
- The client persists `lastSyncedVersion` in DataStore Preferences.
- The heavy exercise payload is only retrieved when `remoteVersion > localVersion`.

### 8.4 Content vs. User Progress Separation
- **Static Content Tables** (`questions`, `options`): Updated via `@Upsert` using deterministic IDs (e.g., `"kt_basics_01"`).
- **User State Tables** (`user_progress`, `streak`): Track completion status, attempt history, and streaks.
- **Guarantee**: Syncing new exercises or content revisions never modifies or resets student learning history.

### 8.5 Background Execution via WorkManager
- Sync tasks are handled as scheduled background jobs through `WorkManager` with network constraints (`NetworkType.CONNECTED`), plus an optional manual "Check for new exercises" trigger in Settings.

