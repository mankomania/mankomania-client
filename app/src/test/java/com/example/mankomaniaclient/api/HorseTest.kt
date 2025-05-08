package com.example.mankomaniaclient.api.HorseTest

import com.example.mankomaniaclient.api.Horse
import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.google.gson.JsonSyntaxException

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
        assertEquals("black", horse.color)
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

    // Additional tests to improve coverage

    @Test
    fun testHorseCopy() {
        val originalHorse = Horse(id = 1, name = "Horse 1", color = "blue")

        // Test copy with no changes
        val horseCopy1 = originalHorse.copy()
        assertEquals(originalHorse, horseCopy1)
        assertEquals(originalHorse.hashCode(), horseCopy1.hashCode())

        // Test copy with one parameter changed
        val horseCopy2 = originalHorse.copy(name = "New Horse Name")
        assertEquals(1, horseCopy2.id)
        assertEquals("New Horse Name", horseCopy2.name)
        assertEquals("blue", horseCopy2.color)

        // Test copy with multiple parameters changed
        val horseCopy3 = originalHorse.copy(id = 99, color = "green")
        assertEquals(99, horseCopy3.id)
        assertEquals("Horse 1", horseCopy3.name)
        assertEquals("green", horseCopy3.color)
    }

    @Test
    fun testToStringImplementation() {
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")
        val stringRepresentation = horse.toString()

        // Verify that toString contains all the properties
        assertTrue(stringRepresentation.contains("id=1"))
        assertTrue(stringRepresentation.contains("name=Horse 1"))
        assertTrue(stringRepresentation.contains("color=blue"))
    }

    @Test
    fun testJsonSerializationRoundTrip() {
        // Test that a horse can be serialized and then deserialized back to the original object
        val originalHorse = Horse(id = 42, name = "Seabiscuit", color = "brown")
        val json = originalHorse.toJson()
        val deserializedHorse = Horse.fromJson(json)

        assertEquals(originalHorse, deserializedHorse)
    }

    @Test
    fun testDeserializationWithMissingFields() {
        // Test deserialization with partial JSON (missing color)
        val partialJson = """{"id":5,"name":"Partial Horse"}"""
        val partialHorse = Horse.fromJson(partialJson)

        assertEquals(5, partialHorse.id)
        assertEquals("Partial Horse", partialHorse.name)
        // Depending on Gson behavior, color might be null or default value
        // We can at least verify it doesn't throw an exception
    }

    @Test
    fun testDeserializationWithExtraFields() {
        // Test deserialization with extra fields in JSON
        val extraFieldsJson = """{"id":7,"name":"Extra Horse","color":"yellow","speed":42,"owner":"John"}"""
        val extraFieldsHorse = Horse.fromJson(extraFieldsJson)

        assertEquals(7, extraFieldsHorse.id)
        assertEquals("Extra Horse", extraFieldsHorse.name)
        assertEquals("yellow", extraFieldsHorse.color)
    }

    @Test
    fun testHorseComponentFunctions() {
        val horse = Horse(id = 10, name = "Component Test", color = "purple")

        // Testing component functions (automatically generated for data classes)
        assertEquals(10, horse.component1())
        assertEquals("Component Test", horse.component2())
        assertEquals("purple", horse.component3())

        // Test destructuring
        val (id, name, color) = horse
        assertEquals(10, id)
        assertEquals("Component Test", name)
        assertEquals("purple", color)
    }

    @Test
    fun testInvalidJsonDeserialization() {
        // Test behavior with invalid JSON
        val invalidJson = """{"id":"not-a-number","name":123,"color":true}"""

        try {
            Horse.fromJson(invalidJson)
            fail("Should have thrown JsonSyntaxException")
        } catch (e: JsonSyntaxException) {
            // Expected exception, test passes
        } catch (e: Exception) {
            fail("Unexpected exception type: ${e.javaClass.name}")
        }
    }
}