package com.code.Quiz.core.ui.component.mascot

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit

@Suppress("DEPRECATION")
@Composable
fun CodeQuizRiveMascot(
    state: MascotState,
    @RawRes resourceId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = "CodeQuiz Mascot"
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) return

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                contentDescription?.let { this.contentDescription = it }
                try {
                    setRiveResource(
                        resId = resourceId,
                        autoplay = true,
                        fit = Fit.CONTAIN,
                        alignment = Alignment.CENTER
                    )
                } catch (_: Exception) {
                    // Resource initialization fallback
                }
            }
        },
        update = { view ->
            try {
                view.setNumberState(
                    RiveMascotConstants.STATE_MACHINE_NAME,
                    RiveMascotConstants.INPUT_STATE_INDEX,
                    state.toRiveStateIndex()
                )
            } catch (_: Exception) {
                // Ignore missing input state gracefully
            }
        },
        onRelease = { view ->
            try {
                view.pause()
                view.stop()
            } catch (_: Exception) {
                // Clean release
            }
        }
    )
}
