package com.code.Quiz.core.ui.component.mascot

import org.junit.Assert.assertEquals
import org.junit.Test

class RiveMascotConstantsTest {

    @Test
    fun verifyRiveMascotConstants() {
        assertEquals("Raccoon", RiveMascotConstants.ARTBOARD_NAME)
        assertEquals("MascotStateMachine", RiveMascotConstants.STATE_MACHINE_NAME)
        assertEquals("stateIndex", RiveMascotConstants.INPUT_STATE_INDEX)
    }

    @Test
    fun verifyMascotStateToRiveStateIndexMapping() {
        assertEquals(0f, MascotState.IDLE.toRiveStateIndex(), 0.001f)
        assertEquals(1f, MascotState.THINKING.toRiveStateIndex(), 0.001f)
        assertEquals(2f, MascotState.SUCCESS.toRiveStateIndex(), 0.001f)
        assertEquals(3f, MascotState.ERROR.toRiveStateIndex(), 0.001f)
        assertEquals(4f, MascotState.CELEBRATION.toRiveStateIndex(), 0.001f)
    }
}
