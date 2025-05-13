package com.example.mankomaniaclient.api

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class HorseSelectionRequestTest {

    @Test
    fun testConstructor() {
        // Arrange & Act
        val request = HorseSelectionRequest("player123", 5)

        // Assert
        assertEquals("player123", request.playerId)
        assertEquals(5, request.horseId)
    }
    @Test
    fun testJsonRoundTrip() {
        val original = HorseSelectionRequest("abc123", 2)
        val json = Json.encodeToString(HorseSelectionRequest.serializer(), original)
        val result = Json.decodeFromString(HorseSelectionRequest.serializer(), json)

        assertEquals(original, result)
    }

    @Test
    fun testDataClassEquality() {
        // Arrange
        val request1 = HorseSelectionRequest("player123", 5)
        val request2 = HorseSelectionRequest("player123", 5)
        val request3 = HorseSelectionRequest("player456", 5)

        // Assert
        assertEquals(request1, request2) // Same values should be equal
        assertNotEquals(request1, request3) // Different values should not be equal
    }

    @Test
    fun testDataClassCopy() {
        // Arrange
        val request1 = HorseSelectionRequest("player123", 5)

        // Act
        val request2 = request1.copy(horseId = 8)

        // Assert
        assertEquals("player123", request2.playerId) // Unchanged field should remain the same
        assertEquals(8, request2.horseId) // Changed field should be updated
    }

    @Test
    fun testEmptyValues() {
        // Arrange & Act
        val request = HorseSelectionRequest("", 0)

        // Assert
        assertEquals("", request.playerId)
        assertEquals(0, request.horseId)
    }

    @Test
    fun testLongValues() {
        // Arrange & Act
        val longPlayerId = "a".repeat(1000)
        val largeHorseId = Int.MAX_VALUE
        val request = HorseSelectionRequest(longPlayerId, largeHorseId)

        // Assert
        assertEquals(longPlayerId, request.playerId)
        assertEquals(largeHorseId, request.horseId)
    }

    @Test
    fun testToString() {
        // Arrange
        val request = HorseSelectionRequest("player123", 5)

        // Act
        val stringRepresentation = request.toString()

        // Assert
        assertTrue(stringRepresentation.contains("playerId=player123"))
        assertTrue(stringRepresentation.contains("horseId=5"))
    }

    @Test
    fun testComponentFunctions() {
        // Arrange
        val request = HorseSelectionRequest("player123", 5)

        // Act & Assert
        assertEquals("player123", request.component1())
        assertEquals(5, request.component2())

        // Test destructuring
        val (playerId, horseId) = request
        assertEquals("player123", playerId)
        assertEquals(5, horseId)
    }
}

