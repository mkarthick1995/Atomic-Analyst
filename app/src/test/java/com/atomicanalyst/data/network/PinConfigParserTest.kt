package com.atomicanalyst.data.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinConfigParserTest {
    @Test
    fun parse_BlankReturnsEmpty() {
        assertTrue(PinConfigParser.parse("").isEmpty())
    }

    @Test
    fun parse_ValidPins() {
        val raw = "api.example.com=sha256/abc123,cdn.example.com=sha256/def456"
        val pins = PinConfigParser.parse(raw)

        assertEquals(2, pins.size)
        assertEquals("api.example.com", pins[0].host)
        assertEquals("sha256/abc123", pins[0].sha256)
    }

    @Test(expected = IllegalArgumentException::class)
    fun parse_InvalidEntryThrows() {
        PinConfigParser.parse("api.example.com")
    }
}
