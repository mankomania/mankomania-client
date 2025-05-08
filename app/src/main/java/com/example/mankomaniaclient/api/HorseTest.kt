package com.example.mankomaniaclient.api

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HorseTest {

    private val gson = Gson()

    @Test
    fun testHorseSerialization() {
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")
        val json = gson.toJson(horse)

        println("Serialized JSON: $json")

        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"name\":\"Horse 1\""))
        assertTrue(json.contains("\"color\":\"blue\""))
    }
}