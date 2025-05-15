package com.example.mankomaniaclient

import android.content.Intent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

class StartingMoneyUtilsTest {

    @Test
    @DisplayName("Should return playerId when intent contains it")
    fun returnsPlayerIdFromIntent() {
        val intent = mockk<Intent>()
        every { intent.getStringExtra("playerId") } returns "test-player-id"

        val result = extractPlayerId(intent)

        assertEquals("test-player-id", result)
    }

    @Test
    @DisplayName("Should return default-player when intent is null")
    fun returnsDefaultPlayerIdWhenIntentIsNull() {
        val result = extractPlayerId(null)
        assertEquals("default-player", result)
    }

    @Test
    @DisplayName("Should return default-player when playerId extra is missing")
    fun returnsDefaultPlayerIdWhenExtraMissing() {
        val intent = mockk<Intent>()
        every { intent.getStringExtra("playerId") } returns null

        val result = extractPlayerId(intent)

        assertEquals("default-player", result)
    }
}