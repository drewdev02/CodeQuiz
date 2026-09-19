package com.code.Quiz.feature.onboarding.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.code.Quiz.core.theme.AccentPurple
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.core.theme.DarkBackground
import com.code.Quiz.core.theme.DarkSurfaceBorder
import com.code.Quiz.core.theme.Spacing
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary
import com.code.Quiz.core.ui.component.button.CodeQuizButton
import com.code.Quiz.core.ui.component.button.CodeQuizButtonVariant
import com.code.Quiz.core.ui.component.mascot.CodeQuizMascot
import com.code.Quiz.core.ui.component.mascot.MascotState
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val description: String,
    val mascotState: MascotState
)

val onboardingSlides = listOf(
    OnboardingSlide(
        title = "Cursos en tu bolsillo",
        description = "Descarga cursos y disfrútalos aún sin conexión a internet.",
        mascotState = MascotState.THINKING
    ),
    OnboardingSlide(
        title = "100% Interactivo",
        description = "Aprende programando con micro-retos prácticos diarios. Cero videos aburridos.",
        mascotState = MascotState.SUCCESS
    ),
    OnboardingSlide(
        title = "Aprende a tu ritmo",
        description = "Lecciones de 3 a 5 minutos diseñadas para crear el hábito diario.",
        mascotState = MascotState.IDLE
    ),
    OnboardingSlide(
        title = "Construye tu racha",
        description = "Gana puntos de experiencia (XP), mantén encendido tu fuego diario y desbloquea logros.",
        mascotState = MascotState.CELEBRATION
    )
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    slides: List<OnboardingSlide> = onboardingSlides,
    onFinish: () -> Unit = {},
    onSkip: () -> Unit = onFinish
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CodeQuizMascot(
                        state = slide.mascotState,
                        modifier = Modifier
                            .size(280.dp)
                            .padding(Spacing.sm)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }
            }

            // Bottom bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left slot: Omitir button (Ghost)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (pagerState.currentPage < slides.lastIndex) {
                        CodeQuizButton(
                            text = "Omitir",
                            onClick = onSkip,
                            variant = CodeQuizButtonVariant.Ghost
                        )
                    }
                }

                // Center slot: Animated pager dots
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) Spacing.lg else Spacing.sm,
                            label = "dot_width_$index"
                        )
                        val dotColor = if (isSelected) AccentPurple else DarkSurfaceBorder

                        Box(
                            modifier = Modifier
                                .padding(horizontal = Spacing.xxs)
                                .height(Spacing.sm)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(Spacing.xs))
                                .background(dotColor)
                        )
                    }
                }

                // Right slot: Siguiente / Empezar button (Primary)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val isLastPage = pagerState.currentPage == slides.lastIndex
                    CodeQuizButton(
                        text = if (isLastPage) "Empezar" else "Siguiente",
                        onClick = {
                            if (isLastPage) {
                                onFinish()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        variant = CodeQuizButtonVariant.Primary
                    )
                }
            }
        }
    }
}

@Preview(name = "Onboarding Screen Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun OnboardingScreenPreview() {
    CodeQuizTheme {
        OnboardingScreen()
    }
}
