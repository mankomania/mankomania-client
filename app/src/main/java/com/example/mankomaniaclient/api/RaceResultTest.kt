package com.example.mankomaniaclient.api

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RaceResultTest {

    private val gson = Gson()

    @Test
    fun testRaceResultSerialization() {
        val result = RaceResult(winningHorseId = 1, playerWon = true, amountWon = 500)
        val json = gson.toJson(result)

        assertTrue(json.contains("\"winningHorseId\":1"))
        assertTrue(json.contains("\"playerWon\":true"))
        assertTrue(json.contains("\"amountWon\":500"))
    }

    @Test
    fun testRaceResultDeserialization() {
        val json = """{"winningHorseId":2,"playerWon":false,"amountWon":0}"""
        val result = gson.fromJson(json, RaceResult::class.java)

        assertEquals(2, result.winningHorseId)
        assertFalse(result.playerWon)
        assertEquals(0, result.amountWon)
    }
}