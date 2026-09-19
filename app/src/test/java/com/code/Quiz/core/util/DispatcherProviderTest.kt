package com.code.Quiz.core.util

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class DispatcherProviderTest {

    @Test
    fun defaultDispatcherProvider_providesStandardDispatchers() {
        val provider: DispatcherProvider = DefaultDispatcherProvider()
        assertEquals(Dispatchers.Main, provider.main)
        assertEquals(Dispatchers.IO, provider.io)
        assertEquals(Dispatchers.Default, provider.default)
        assertEquals(Dispatchers.Unconfined, provider.unconfined)
    }
}
