# Rive Mascot Animation Integration Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Integrate the Rive Android runtime (`app.rive:rive-android`) into CodeQuiz to enable skeletal bone animation, interactive state machine transitions, and frame-by-frame puppet movements for the raccoon mascot (tail sway, blinking, articulated paws), matching Duolingo's visual standard.

**Architecture:** Encapsulate the Rive runtime inside a dedicated Compose adapter ([`CodeQuizRiveMascot.kt`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizRiveMascot.kt)) that coordinates with the existing [`CodeQuizMascot.kt`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizMascot.kt) API. The application-level contracts ([`MascotState`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/src/main/java/com/code/Quiz/core/ui/component/mascot/MascotState.kt)) remain completely decoupled from the animation engine, with automatic fallback to Lottie and static drawables for Android Studio `@Preview` environments or environments without compiled binary assets.

**Tech Stack:** Kotlin 2.2, Jetpack Compose (Material 3), `app.rive:rive-android:11.15.0`, `androidx.startup:startup-runtime:1.2.0`, JUnit 4, Gradle Version Catalogs (`libs.versions.toml`).

**Spec:** [`docs/superpowers/specs/raccoon_rive_rig_spec.md`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/docs/superpowers/specs/raccoon_rive_rig_spec.md) (Artboard, Bone hierarchy, and State Machine specification).

## Global Constraints
- Strictly follow `AGENTS.md` Package-by-Feature and Core Design System boundaries: all mascot components reside in `com.code.Quiz.core.ui.component.mascot.*`.
- ViewModels and UI screens must NOT import Rive classes directly; they interact exclusively via `CodeQuizMascot(state = MascotState.*)`.
- Android Studio Compose `@Preview` must never fail or throw `UnsatisfiedLinkError` when rendering composables using the mascot.
- Zero usage of `!!` (double-bang) assertions in any new Kotlin code.
- All unit tests must execute in under 1 second without native C++ crashes.

## Review Focus
1. **Native JNI crash in Preview:** Android Studio previews cannot run native C++ Rive binaries; verify `LocalInspectionMode.current` guards against invoking `RiveAnimationView`.
2. **Missing/Corrupted Asset Fallback:** If `raccoon_mascot.riv` is missing or fails to parse, the component must silently and cleanly fall back to Lottie or drawables without throwing an unhandled exception.
3. **Rapid State Transitions:** When the user taps through questions quickly (`THINKING` -> `SUCCESS` -> `THINKING`), state machine inputs must update reactively without blocking the main UI thread.
4. **Memory Leaks & Lifecycle Disposal:** Rive's C++ controller must release its native memory (`destroy()`) when the composable leaves the composition.
5. **State Machine Input Name Parity:** Input strings (`stateIndex`, `triggerSuccess`, `triggerError`) must match exactly between Kotlin constants and Rive runtime inspection.

---

### Task 1: Rive Android Runtime Dependencies & Gradle Setup

**Files:**
- Modify: [`gradle/libs.versions.toml:15-52`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/gradle/libs.versions.toml#L15-L52)
- Modify: [`app/build.gradle.kts:50-79`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/build.gradle.kts#L50-L79)

**Interfaces:**
- Consumes: Maven Central repository configuration.
- Produces: `app.rive:rive-android` and `androidx.startup:startup-runtime` available in `:app` compile classpath.

- [ ] **Step 1: Add Rive and Startup to `libs.versions.toml`**

Add version and library definitions:
```toml
[versions]
rive-android = "11.15.0"
startup-runtime = "1.2.0"

[libraries]
rive-android = { module = "app.rive:rive-android", version.ref = "rive-android" }
androidx-startup = { module = "androidx.startup:startup-runtime", version.ref = "startup-runtime" }
```

- [ ] **Step 2: Add implementation dependencies to `app/build.gradle.kts`**

Add `implementation(libs.rive.android)` and `implementation(libs.androidx.startup)`.

- [ ] **Step 3: Run Gradle compilation to verify dependency resolution**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL (dependencies downloaded and verified).

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts
git commit -m "build(deps): add Rive Android runtime and Jetpack Startup dependencies"
```

---

### Task 2: Mascot State Machine Controller & Mapping

**Files:**
- Create: `app/src/main/java/com/code/Quiz/core/ui/component/mascot/RiveMascotConstants.kt`
- Create: `app/src/test/java/com/code/Quiz/core/ui/component/mascot/RiveMascotConstantsTest.kt`

**Interfaces:**
- Consumes: [`MascotState`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/src/main/java/com/code/Quiz/core/ui/component/mascot/MascotState.kt)
- Produces: `fun MascotState.toRiveStateIndex(): Float` and constants `RiveMascotConstants.ARTBOARD_NAME`, `RiveMascotConstants.STATE_MACHINE_NAME`, `RiveMascotConstants.INPUT_STATE_INDEX`.

- [ ] **Step 1: Write unit tests for `MascotState.toRiveStateIndex()`**

Write test verifying:
- `MascotState.IDLE` -> `0f`
- `MascotState.THINKING` -> `1f`
- `MascotState.SUCCESS` -> `2f`
- `MascotState.ERROR` -> `3f`
- `MascotState.CELEBRATION` -> `4f`

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.core.ui.component.mascot.RiveMascotConstantsTest`
Expected: FAIL (unresolved reference `toRiveStateIndex`).

- [ ] **Step 3: Implement `RiveMascotConstants.kt`**

Define:
```kotlin
package com.code.Quiz.core.ui.component.mascot

object RiveMascotConstants {
    const val ARTBOARD_NAME = "Raccoon"
    const val STATE_MACHINE_NAME = "MascotStateMachine"
    const val INPUT_STATE_INDEX = "stateIndex"
}

fun MascotState.toRiveStateIndex(): Float = when (this) {
    MascotState.IDLE -> 0f
    MascotState.THINKING -> 1f
    MascotState.SUCCESS -> 2f
    MascotState.ERROR -> 3f
    MascotState.CELEBRATION -> 4f
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest --tests com.code.Quiz.core.ui.component.mascot.RiveMascotConstantsTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/code/Quiz/core/ui/component/mascot/RiveMascotConstants.kt app/src/test/java/com/code/Quiz/core/ui/component/mascot/RiveMascotConstantsTest.kt
git commit -m "feat(mascot): define Rive state machine constants and state mapping"
```

---

### Task 3: Compose Adapter (`CodeQuizRiveMascot.kt`)

**Files:**
- Create: `app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizRiveMascot.kt`

**Interfaces:**
- Consumes: `RiveAnimationView`, `MascotState`, `RiveMascotConstants`
- Produces: `@Composable fun CodeQuizRiveMascot(state: MascotState, modifier: Modifier, ...)`

- [ ] **Step 1: Implement `CodeQuizRiveMascot.kt` with lifecycle and state updates**

Create composable utilizing `AndroidView`:
```kotlin
@Composable
fun CodeQuizRiveMascot(
    state: MascotState,
    @RawRes resourceId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = "CodeQuiz Mascot"
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) return // Fallback handled by parent CodeQuizMascot

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                setRiveResource(
                    resId = resourceId,
                    artboardName = RiveMascotConstants.ARTBOARD_NAME,
                    stateMachineName = RiveMascotConstants.STATE_MACHINE_NAME,
                    autoplay = true,
                    fit = Fit.CONTAIN,
                    alignment = Alignment.CENTER
                )
            }
        },
        update = { view ->
            view.setNumberState(
                RiveMascotConstants.STATE_MACHINE_NAME,
                RiveMascotConstants.INPUT_STATE_INDEX,
                state.toRiveStateIndex()
            )
        }
    )
}
```

- [ ] **Step 2: Add DisposableEffect to properly release native Rive resources on exit**

Ensure clean native destruction when leaving composition.

- [ ] **Step 3: Compile to verify zero syntax/type errors**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizRiveMascot.kt
git commit -m "feat(mascot): implement CodeQuizRiveMascot Compose adapter with state machine binding"
```

---

### Task 4: Unified Facade & Graceful Fallback in `CodeQuizMascot.kt`

**Files:**
- Modify: [`app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizMascot.kt:20-65`](file:///Users/Andrew/WorkSpace/work/uci/CodeQuiz/app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizMascot.kt#L20-L65)

**Interfaces:**
- Consumes: `CodeQuizRiveMascot`, Lottie pipeline, `MascotState`
- Produces: Unified `@Composable fun CodeQuizMascot(...)` that automatically uses Rive when `raccoon_mascot.riv` is present, Lottie as dynamic fallback, and Drawables as static/preview fallback.

- [ ] **Step 1: Check resource identifier dynamically**

Add helper check to detect if `R.raw.raccoon_mascot` exists in the build assets.

- [ ] **Step 2: Integrate Rive execution branch with fallback chaining**

```kotlin
val hasRiveAsset = remember {
    try {
        R.raw::class.java.getField("raccoon_mascot").getInt(null) > 0
    } catch (_: Exception) {
        false
    }
}

if (!isPreview && hasRiveAsset) {
    CodeQuizRiveMascot(
        state = state,
        resourceId = R.raw.raccoon_mascot,
        modifier = modifier,
        contentDescription = contentDescription
    )
} else {
    // Existing Lottie / Drawable fallback pipeline
}
```

- [ ] **Step 3: Verify all unit tests continue to pass**

Run: `./gradlew testDebugUnitTest`
Expected: 78+ tests passing.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/code/Quiz/core/ui/component/mascot/CodeQuizMascot.kt
git commit -m "refactor(mascot): unite Rive runtime with resilient Lottie and Drawable fallbacks"
```

---

### Task 5: Rive Rig Specification & Asset Pipeline

**Files:**
- Create: `docs/superpowers/specs/raccoon_rive_rig_spec.md`
- Create: `app/src/main/res/raw/raccoon_mascot.riv` (verified working Rive binary asset)

**Interfaces:**
- Consumes: Mascot artwork anatomy (tail, head, body, eyes, arms).
- Produces: Complete technical specification for building or modifying the character in `rive.app` + functional `.riv` binary for runtime execution.

- [ ] **Step 1: Write `docs/superpowers/specs/raccoon_rive_rig_spec.md`**

Detail:
- SVG layer decomposition coordinates and pivot anchors:
  - Tail: pivot at base `(240, 480)`, rotation constraint `[-25°, +25°]`.
  - Torso: pivot at base `(240, 520)`, squash & stretch `[0.95, 1.05]`.
  - Head: pivot at neck `(240, 240)`, rotation `[-8°, +8°]`.
  - Eyes: clipping mask with eyelid translation for 300ms blinks every 3.5s.
  - Left / Right Arms: pivots at shoulders `(150, 310)` and `(330, 310)`.
- State Machine Transitions:
  - `stateIndex == 0` (IDLE): gentle breathing, tail sway, random eye blink.
  - `stateIndex == 1` (THINKING): head tilt, paw on chin, lightbulb animation.
  - `stateIndex == 2` (SUCCESS): paws pump upward, happy eye squint, jump.
  - `stateIndex == 3` (ERROR): paw scratches head, empathetic eye tilt.
  - `stateIndex == 4` (CELEBRATION): party hat appears, dancing loop, confetti burst.

- [ ] **Step 2: Add validated `.riv` asset to `app/src/main/res/raw/`**

Place `raccoon_mascot.riv` in raw resources directory.

- [ ] **Step 3: Commit**

```bash
git add docs/superpowers/specs/raccoon_rive_rig_spec.md app/src/main/res/raw/raccoon_mascot.riv
git commit -m "feat(mascot): add Rive character rig specification and mascot .riv binary asset"
```

---

### Task 6: Full Verification & Device Validation

**Files:**
- None (Verification step)

- [ ] **Step 1: Execute all unit tests**

Run: `./gradlew testDebugUnitTest`
Expected: All tests pass.

- [ ] **Step 2: Build debug APK**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL with `.apk` produced in `app/build/outputs/apk/debug/`.

- [ ] **Step 3: Install and verify on running emulator**

Run: `adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`
Navigate to Roadmap, Quiz, and Progress screens. Verify smooth 60 FPS animation, responsive state transitions on right/wrong answers, and zero memory leaks.

- [ ] **Step 4: Final commit and audit log update**

Update `AUDIT.md` with Rive runtime integration details.
