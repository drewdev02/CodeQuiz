package com.code.Quiz.core.ui.component.mascot

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Rive

@Suppress("DEPRECATION")
@Composable
fun CodeQuizRiveMascot(
    state: MascotState,
    @RawRes resourceId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = "CodeQuiz Mascot",
    onError: (() -> Unit)? = null
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) return

    AndroidView(
        modifier = modifier,
        factory = { context ->
            try {
                Rive.init(context.applicationContext)
                RiveAnimationView(context).apply {
                    contentDescription?.let { this.contentDescription = it }
                    setRiveResource(
                        resId = resourceId,
                        artboardName = RiveMascotConstants.ARTBOARD_NAME,
                        stateMachineName = RiveMascotConstants.STATE_MACHINE_NAME,
                        autoplay = true,
                        fit = Fit.CONTAIN,
                        alignment = Alignment.CENTER
                    )
                }
            } catch (t: Throwable) {
                android.util.Log.w("CodeQuizRiveMascot", "Failed to create RiveAnimationView, fallback active", t)
                onError?.invoke()
                android.view.View(context)
            }
        },
        update = { view ->
            if (view is RiveAnimationView) {
                try {
                    val artboard = view.controller.file?.firstArtboard
                    val stateMachineName = RiveMascotConstants.STATE_MACHINE_NAME
                    if (artboard != null && artboard.stateMachineNames.contains(stateMachineName)) {
                        view.setNumberState(
                            stateMachineName,
                            RiveMascotConstants.INPUT_STATE_INDEX,
                            state.toRiveStateIndex()
                        )
                    }
                } catch (_: Throwable) {
                    // Ignore missing input state gracefully
                }
            }
        },
        onRelease = { view ->
            if (view is RiveAnimationView) {
                try {
                    view.pause()
                    view.stop()
                } catch (_: Throwable) {
                    // Clean release
                }
            }
        }
    )
}
