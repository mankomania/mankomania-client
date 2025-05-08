package com.example.mankomaniaclient.api.HorseTest

import com.example.mankomaniaclient.api.Horse
import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HorseTest {

    private val gson = Gson()

    @Test
    fun testHorseProperties() {
        // Test constructor and properties
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")

        assertEquals(1, horse.id)
        assertEquals("Horse 1", horse.name)
        assertEquals("blue", horse.color)
    }

    @Test
    fun testHorseSerialization() {
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")
        val json = horse.toJson() // Use the class method instead of direct gson usage

        println("Serialized JSON: $json")

        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"name\":\"Horse 1\""))
        assertTrue(json.contains("\"color\":\"blue\""))
    }

    @Test
    fun testHorseDeserialization() {
        val jsonString = """{"id":2,"name":"Thunder","color":"black"}"""

        // Test using the companion method
        val horse = Horse.fromJson(jsonString)
        assertEquals(2, horse.id)
        assertEquals("Thunder", horse.name)
        assertEquals("black", horse.color)

        // Also test using direct deserialization with Gson
        val horseFromGson = gson.fromJson(jsonString, Horse::class.java)
        assertEquals(2, horseFromGson.id)
        assertEquals("Thunder", horseFromGson.name)
        assertEquals("black", horseFromGson.color)
    }

    @Test
    fun testHorseEquality() {
        val horse1 = Horse(id = 1, name = "Horse 1", color = "blue")
        val horse2 = Horse(id = 1, name = "Horse 1", color = "blue")
        val horse3 = Horse(id = 2, name = "Horse 2", color = "red")

        // Test equals() and hashCode() (automatically generated for data classes)
        assertEquals(horse1, horse2)
        assertTrue(horse1 != horse3)
    }
}