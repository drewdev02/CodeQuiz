# DESIGN.md — Visual Design System & Color Palette

This document defines the official visual design language, color tokens, typography, and component specifications for the **CodeQuiz** Android application, based on the approved dark-theme UI reference.

---

## 1. Design Philosophy

- **Aesthetic**: Modern Developer / IDE Dark Mode (Cyberpunk & Obsidian Slate).
- **Theme Mode**: Dark-First / Dark-Exclusive experience optimized for long reading sessions, OLED power efficiency, and code legibility.
- **Visual Hierarchy**: Deep obsidian navy background layers with elevated glass/card surfaces, energized by high-contrast neon accents (electric cyan, neon purple, emerald mint, and flame orange).

---

## 2. Color Palette & Design Tokens

### 2.1 Background & Surface Colors
| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `DarkBackground` | `#020813` | Root app background / scaffold canvas |
| `DarkSurface` | `#0A1124` | Primary cards, content panels, containers |
| `DarkSurfaceElevated` | `#0E172A` | Nested metric cards, stat items, active elements |
| `DarkSurfaceBorder` | `#162036` | Subtle 1dp borders on cards and dividers |
| `CodeBlockBackground` | `#070D1C` | Monospace code snippet container background |

### 2.2 Brand & Primary Accent Colors
| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `AccentPurple` | `#6366F1` | Primary brand color, active tab background, selected state |
| `AccentPurpleDark` | `#232570` | Active pill tab container background |
| `AccentCyan` | `#00D2FF` | Secondary accent, progress bars, code token highlights |
| `AccentBlue` | `#3B82F6` | Informational badges, language skill bars |

### 2.3 Semantic & Gamification Colors
| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `SuccessMint` | `#00F59B` | Correct answers, streak completed days, "+XP" chips, "Great job!" text |
| `ErrorRed` | `#FF4757` | Incorrect answers, syntax errors, heart/life lost |
| `StreakOrange` | `#FF6B00` | Daily streak fire badge, milestone highlights |
| `StreakPillBackground`| `#1F140A` | Subtle dark amber container for top streak pill |

### 2.4 Typography & Text Colors
| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `TextPrimary` | `#FFFFFF` | Headings, card titles, active tab text, code content |
| `TextSecondary` | `#94A3B8` | Subtitles, metric labels, inactive descriptions |
| `TextMuted` | `#64748B` | Inactive bottom navigation icons and helper hints |

### 2.5 Gradients
- **Primary Hero Gradient** (`GradientHero`):
  `Brush.horizontalGradient(listOf(Color(0xFF00D2FF), Color(0xFF7B61FF)))`  
  *Used for: Circular progress ring, overall progress bar, milestone celebrations.*
- **Purple Glow Gradient** (`GradientPurpleGlow`):
  `Brush.verticalGradient(listOf(Color(0xFF6366F1).copy(alpha = 0.2f), Color.Transparent))`  
  *Used for: Active bottom navigation item highlight and active cards.*

---

## 3. Component Design Specifications

### 3.1 Top App Bar & Streak Pill
- **Title**: Centered screen title in `TextPrimary` (SemiBold, 18sp).
- **Streak Pill**: Rounded pill chip (`RoundedCornerShape(20.dp)`) in the top-right corner.
  - Background: `StreakPillBackground` (`#1F140A`).
  - Content: Fire icon `🔥` in `StreakOrange` (`#FF6B00`) + streak count in `StreakOrange`.

### 3.2 Segmented Pill Tab Bar
- Outer container: `DarkSurface` (`#0A1124`) with `RoundedCornerShape(12.dp)` and 4dp internal padding.
- Inactive tabs: Transparent background, `TextSecondary` label.
- Active tab: Background `AccentPurpleDark` (`#232570`), text `TextPrimary` (`#FFFFFF`, Medium, 14sp).

### 3.3 Metric & Statistic Cards
- Four-column grid or cards with:
  - Background: `DarkSurfaceElevated` (`#0E172A`).
  - Border: 1dp solid `DarkSurfaceBorder` (`#162036`).
  - Shape: `RoundedCornerShape(12.dp)`.
  - Icon badge: Circular or rounded square with semantic tint (Cyan for Courses, Mint for Lessons, Purple for Problems, Orange for Time).

### 3.4 Progress Rings & Bars
- Track background: `DarkSurfaceElevated` (`#0E172A`).
- Fill: `GradientHero` (`#00D2FF` -> `#7B61FF`).
- Corner caps: Rounded (`StrokeCap.Round`).

### 3.5 Learning Streak Tracker
- Horizontal row showing 7 days (Mon — Sun).
- Completed day: Mint circle with checkmark icon (`#00F59B`).
- Current / Active day: Glowing circle with flame icon (`#FF6B00`).
- Upcoming / Inactive day: Dark circular outline with muted day abbreviation.

### 3.6 Interactive Question & Code Snippet Cards
- **Code Card**:
  - Background: `CodeBlockBackground` (`#070D1C`).
  - Border: 1dp solid `DarkSurfaceBorder` (`#162036`).
  - Typography: Monospace font (JetBrains Mono / Roboto Mono), 13sp.
- **Answer Option Cards**:
  - Default: `DarkSurface` background, 1dp border `DarkSurfaceBorder`.
  - Selected (Pending): 1.5dp border `AccentPurple` (`#6366F1`), subtle background tint.
  - Validated Correct: 1.5dp border `SuccessMint` (`#00F59B`), subtle mint background glow.
  - Validated Incorrect: 1.5dp border `ErrorRed` (`#FF4757`), subtle red background glow.

### 3.7 Bottom Navigation Bar
- Background: `DarkBackground` (`#020813`) with subtle top border `DarkSurfaceBorder`.
- Inactive items: `TextMuted` (`#64748B`).
- Active item: Icon & label tinted with `AccentPurple` (`#6366F1`) and a 2dp horizontal indicator line below the label.

---

## 4. Jetpack Compose Theme Implementation

Ready-to-use definitions for `core/theme/Color.kt` and `core/theme/Theme.kt`:

### 4.1 `Color.kt`
```kotlin
package com.code.Quiz.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Background & Surfaces
val DarkBackground = Color(0xFF020813)
val DarkSurface = Color(0xFF0A1124)
val DarkSurfaceElevated = Color(0xFF0E172A)
val DarkSurfaceBorder = Color(0xFF162036)
val CodeBlockBackground = Color(0xFF070D1C)

// Accents
val AccentPurple = Color(0xFF6366F1)
val AccentPurpleDark = Color(0xFF232570)
val AccentCyan = Color(0xFF00D2FF)
val AccentBlue = Color(0xFF3B82F6)

// Semantics & Gamification
val SuccessMint = Color(0xFF00F59B)
val ErrorRed = Color(0xFFFF4757)
val StreakOrange = Color(0xFFFF6B00)
val StreakPillBackground = Color(0xFF1F140A)

// Text
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val HeroGradient = Brush.horizontalGradient(
    colors = listOf(AccentCyan, Color(0xFF7B61FF))
)
```

### 4.2 Material 3 ColorScheme (`Theme.kt`)
```kotlin
package com.code.Quiz.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = TextPrimary,
    primaryContainer = AccentPurpleDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun CodeQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
```
