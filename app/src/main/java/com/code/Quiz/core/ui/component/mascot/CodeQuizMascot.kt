package com.code.Quiz.core.ui.component.mascot

import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.code.Quiz.R
import com.code.Quiz.core.theme.CodeQuizTheme

@Composable
fun CodeQuizMascot(
    state: MascotState,
    modifier: Modifier = Modifier,
    contentDescription: String? = "CodeQuiz Mascot",
    contentScale: ContentScale = ContentScale.Fit
) {
    val isPreview = LocalInspectionMode.current

    val riveResId = androidx.compose.runtime.remember {
        try {
            R.raw::class.java.getField("raccoon_mascot").getInt(null)
        } catch (_: Exception) {
            0
        }
    }

    if (!isPreview && riveResId != 0) {
        CodeQuizRiveMascot(
            state = state,
            resourceId = riveResId,
            modifier = modifier,
            contentDescription = contentDescription
        )
        return
    }

    @RawRes val resId = when (state) {
        MascotState.IDLE -> R.raw.raccoon_idle
        MascotState.THINKING -> R.raw.raccoon_thinking
        MascotState.SUCCESS -> R.raw.raccoon_success
        MascotState.ERROR -> R.raw.raccoon_error
        MascotState.CELEBRATION -> R.raw.raccoon_celebration
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))

    val fallbackResId = when (state) {
        MascotState.IDLE -> R.drawable.mascot_raccoon_idle
        MascotState.THINKING -> R.drawable.mascot_raccoon_thinking
        MascotState.SUCCESS -> R.drawable.mascot_raccoon_success
        MascotState.ERROR -> R.drawable.mascot_raccoon_error
        MascotState.CELEBRATION -> R.drawable.mascot_raccoon_celebration
    }

    if (isPreview || composition == null) {
        Image(
            painter = painterResource(id = fallbackResId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

@Preview(name = "Mascot States Preview", showBackground = true, backgroundColor = 0xFF020813)
@Composable
private fun CodeQuizMascotPreview() {
    CodeQuizTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CodeQuizMascot(
                    state = MascotState.IDLE,
                    modifier = Modifier.size(120.dp)
                )
                CodeQuizMascot(
                    state = MascotState.THINKING,
                    modifier = Modifier.size(120.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CodeQuizMascot(
                    state = MascotState.SUCCESS,
                    modifier = Modifier.size(120.dp)
                )
                CodeQuizMascot(
                    state = MascotState.ERROR,
                    modifier = Modifier.size(120.dp)
                )
                CodeQuizMascot(
                    state = MascotState.CELEBRATION,
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}
