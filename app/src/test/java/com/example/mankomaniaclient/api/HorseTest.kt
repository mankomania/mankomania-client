package com.example.mankomaniaclient.api

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
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

    @Test
    fun testHorseDeserialization() {
        val json = """{"id":2,"name":"Horse 2","color":"red"}"""
        val horse = gson.fromJson(json, Horse::class.java)

        assertEquals(2, horse.id)
        assertEquals("Horse 2", horse.name)
        assertEquals("red", horse.color)
    }
}
