package com.example.mankomaniaclient.api

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HorseSelectionRequestTest {

    private val gson = Gson()

    @Test
    fun testHorseSelectionRequestSerialization() {
        val request = HorseSelectionRequest(playerId = "player-001", horseId = 2)
        val json = gson.toJson(request)

        assertTrue(json.contains("\"playerId\":\"player-001\""))
        assertTrue(json.contains("\"horseId\":2"))
    }

    @Test
    fun testHorseSelectionRequestDeserialization() {
        val json = """{"playerId":"player-001","horseId":2}"""
        val request = gson.fromJson(json, HorseSelectionRequest::class.java)

        assertEquals("player-001", request.playerId)
        assertEquals(2, request.horseId)
    }
}
