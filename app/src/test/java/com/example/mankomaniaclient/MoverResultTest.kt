package com.example.mankomaniaclient

import com.example.mankomaniaclient.model.MoveResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @file MoveResultTest.kt
 * @author eles17
 * @since 2025-05-13
 * @description Tests for MoveResult serialization/deserialization logic.
 */
class MoveResultTest {

    /**
     * Test case: MoveResult should serialize and deserialize correctly.
     * Ensures no fields are lost or corrupted in the process.
     */
    @Test
    fun testMoveResultSerializationRoundTrip() {
        val original = MoveResult(
            newPosition = 5,
            oldPosition = 3,
            fieldType = "CasinoAction",
            fieldDescription = "Try your luck",
            playersOnField = listOf("Toni", "Jorge")
        )

        val json = Json.encodeToString(original)
        val result = Json.decodeFromString<MoveResult>(json)

        assertEquals(original, result)
    }
}
