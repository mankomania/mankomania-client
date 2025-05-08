package com.example.mankomaniaclient.api

import com.google.gson.Gson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HorseTest {

    private val gson = Gson()

    @Test
    fun testHorseSerialization() {
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")
        val json = gson.toJson(horse)

        println("Serialized JSON: $json")

        assertTrue(json.contains("\"id\":1"), "JSON should contain id:1")
        assertTrue(json.contains("\"name\":\"Horse 1\""), "JSON should contain horse name")
        assertTrue(json.contains("\"color\":\"blue\""), "JSON should contain color:blue")
    }
}