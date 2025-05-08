package com.example.mankomaniaclient.api

import com.google.gson.Gson
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
    fun testToJson() {
        // Arrange
        val request = HorseSelectionRequest("player123", 5)
        val expectedJson = """{"playerId":"player123","horseId":5}"""

        // Act
        val resultJson = request.toJson()

        // Assert
        assertEquals(expectedJson, resultJson)
    }

    @Test
    fun testFromJson() {
        // Arrange
        val json = """{"playerId":"player456","horseId":7}"""

        // Act
        val request = HorseSelectionRequest.fromJson(json)

        // Assert
        assertEquals("player456", request.playerId)
        assertEquals(7, request.horseId)
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
    fun testRoundTripJsonSerialization() {
        // Arrange
        val original = HorseSelectionRequest("player789", 3)

        // Act
        val json = original.toJson()
        val deserialized = HorseSelectionRequest.fromJson(json)

        // Assert
        assertEquals(original, deserialized)
    }

    @Test
    fun testEmptyValues() {
        // Arrange & Act
        val request = HorseSelectionRequest("", 0)
        val json = request.toJson()
        val deserialized = HorseSelectionRequest.fromJson(json)

        // Assert
        assertEquals("", deserialized.playerId)
        assertEquals(0, deserialized.horseId)
        assertEquals(request, deserialized)
    }

    @Test
    fun testLongValues() {
        // Arrange
        val longPlayerId = "a".repeat(1000)
        val largeHorseId = Int.MAX_VALUE

        // Act
        val request = HorseSelectionRequest(longPlayerId, largeHorseId)
        val json = request.toJson()
        val deserialized = HorseSelectionRequest.fromJson(json)

        // Assert
        assertEquals(longPlayerId, deserialized.playerId)
        assertEquals(largeHorseId, deserialized.horseId)
    }
}