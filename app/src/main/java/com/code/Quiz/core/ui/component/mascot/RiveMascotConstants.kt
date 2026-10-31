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
