package com.code.Quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.feature.onboarding.ui.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeQuizTheme {
                OnboardingScreen()
            }
        }
    }
}