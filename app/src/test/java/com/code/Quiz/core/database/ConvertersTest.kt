package com.code.Quiz.core.database

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun date_conversion_roundTrip() {
        val now = Date()
        val timestamp = converters.dateToTimestamp(now)
        val converted = converters.fromTimestamp(timestamp)

        assertEquals(now, converted)
    }

    @Test
    fun date_null_returnsNull() {
        assertNull(converters.dateToTimestamp(null))
        assertNull(converters.fromTimestamp(null))
    }

    @Test
    fun stringList_conversion_roundTrip() {
        val list = listOf("alpha", "beta", "gamma")
        val serialized = converters.fromStringList(list)
        val deserialized = converters.toStringList(serialized)

        assertEquals(list, deserialized)
    }

    @Test
    fun stringList_null_returnsNull() {
        assertNull(converters.fromStringList(null))
        assertNull(converters.toStringList(null))
    }
}
