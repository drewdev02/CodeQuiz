package com.code.Quiz.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Background & Surface Colors
val DarkBackground = Color(0xFF020813)
val DarkSurface = Color(0xFF0A1124)
val DarkSurfaceElevated = Color(0xFF0E172A)
val DarkSurfaceBorder = Color(0xFF162036)
val CodeBlockBackground = Color(0xFF070D1C)

// Brand & Primary Accent Colors
val AccentPurple = Color(0xFF6366F1)
val AccentPurpleDark = Color(0xFF232570)
val AccentCyan = Color(0xFF00D2FF)
val AccentBlue = Color(0xFF3B82F6)

// Semantic & Gamification Colors
val SuccessMint = Color(0xFF00F59B)
val ErrorRed = Color(0xFFFF4757)
val StreakOrange = Color(0xFFFF6B00)
val StreakPillBackground = Color(0xFF1F140A)

// Typography & Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val HeroGradient = Brush.horizontalGradient(
    colors = listOf(AccentCyan, Color(0xFF7B61FF))
)
val GradientHero = HeroGradient
val GradientPurpleGlow = Brush.verticalGradient(
    colors = listOf(AccentPurple.copy(alpha = 0.2f), Color.Transparent)
)
