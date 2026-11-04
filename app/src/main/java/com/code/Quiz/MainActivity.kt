package com.code.Quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.code.Quiz.core.theme.CodeQuizTheme
import com.code.Quiz.navigation.AppNavGraph
import com.code.Quiz.navigation.AppNavViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val appNavViewModel: AppNavViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            appNavViewModel.isOnboardingCompleted.value == null
        }

        setContent {
            CodeQuizTheme {
                AppNavGraph()
            }
        }
    }
}