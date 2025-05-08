package com.example.mankomaniaclient.api

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.google.gson.Gson

class HorseSelectionRequestTest {

    private val gson = Gson()

    @Test
    fun testHorseSelectionRequestSerialization() {
        val request = HorseSelectionRequest(playerId = "player-001", horseId = 2)
        val json = request.toJson()

        // Test that the serialized JSON contains expected fields with correct values
        assertTrue(json.contains("\"playerId\""))
        assertTrue(json.contains("\"player-001\""))
        assertTrue(json.contains("\"horseId\""))
        assertTrue(json.contains("2"))

        // Parse the JSON back to verify structure
        val map = gson.fromJson(json, Map::class.java)
        assertEquals("player-001", map["playerId"])
        assertEquals(2.0, map["horseId"]) // Note: JSON numbers are deserialized as doubles by default
    }

    @Test
    fun testHorseSelectionRequestDeserialization() {
        val json = """{"playerId":"player-001","horseId":2}"""

        // Test using the companion method
        val request = HorseSelectionRequest.fromJson(json)
        assertEquals("player-001", request.playerId)
        assertEquals(2, request.horseId)

        // Also test direct deserialization to ensure consistency
        val requestFromGson = gson.fromJson(json, HorseSelectionRequest::class.java)
        assertEquals("player-001", requestFromGson.playerId)
        assertEquals(2, requestFromGson.horseId)
    }
}