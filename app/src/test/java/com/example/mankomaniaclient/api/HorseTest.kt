package com.example.mankomaniaclient.api.HorseTest

import com.example.mankomaniaclient.api.Horse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HorseTest {

    @Test
    fun testHorseProperties() {
        // Test constructor and properties
        val horse = Horse(id = 1, name = "Horse 1", color = "blue")

        assertEquals(1, horse.id)
        assertEquals("Horse 1", horse.name)
        assertEquals("blue", horse.color)
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
    fun testNullableColor() {
        // Test that color can be null
        val horse = Horse(id = 15, name = "Null Color Horse", color = null)
        assertEquals(15, horse.id)
        assertEquals("Null Color Horse", horse.name)
        assertNull(horse.color)
    }
}