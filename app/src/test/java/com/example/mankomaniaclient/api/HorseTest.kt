package com.example.mankomaniaclient.api

import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

class HorseTest {
    @Test
    @DisplayName("Direct call to Horse.fromJson")
    fun testFromJsonDirectly() {
        // Arrange
        val json = """{"id":99,"name":"Solo","color":"gold"}"""

        // Act
        val horse = Horse.fromJson(json)

        // Assert
        assertEquals(99, horse.id)
        assertEquals("Solo", horse.name)
        assertEquals("gold", horse.color)
    }

    @Test
    @DisplayName("Test basic Horse constructor and properties")
    fun testHorseProperties() {
        // Arrange & Act
        val horse = Horse(id = 1, name = "Midnight", color = "black")

        // Assert
        assertEquals(1, horse.id)
        assertEquals("Midnight", horse.name)
        assertEquals("black", horse.color)
    }

    @Test
    @DisplayName("Test Horse constructor with null color")
    fun testHorseWithNullColor() {
        // Arrange & Act
        val horse = Horse(id = 2, name = "Lightning", color = null)

        // Assert
        assertEquals(2, horse.id)
        assertEquals("Lightning", horse.name)
        assertNull(horse.color)
    }

    @Test
    @DisplayName("Test JSON serialization")
    fun testJsonSerialization() {
        // Arrange
        val horse = Horse(id = 3, name = "Thunder", color = "brown")

        // Act
        val jsonString = horse.toJson()

        // Assert
        assertTrue(jsonString.contains("\"id\":3"))
        assertTrue(jsonString.contains("\"name\":\"Thunder\""))
        assertTrue(jsonString.contains("\"color\":\"brown\""))
    }

    @Test
    @DisplayName("Test JSON deserialization")
    fun testJsonDeserialization() {
        // Arrange
        val jsonString = """{"id":4,"name":"Spirit","color":"white"}"""

        // Act
        val horse = Horse.fromJson(jsonString)

        // Assert
        assertEquals(4, horse.id)
        assertEquals("Spirit", horse.name)
        assertEquals("white", horse.color)
    }

    @Test
    @DisplayName("Test JSON deserialization with null color")
    fun testJsonDeserializationWithNullColor() {
        // Arrange
        val jsonString = """{"id":5,"name":"Shadow","color":null}"""

        // Act
        val horse = Horse.fromJson(jsonString)

        // Assert
        assertEquals(5, horse.id)
        assertEquals("Shadow", horse.name)
        assertNull(horse.color)
    }

    @Test
    @DisplayName("Test JSON deserialization with missing color")
    fun testJsonDeserializationWithMissingColor() {
        // Arrange
        val jsonString = """{"id":6,"name":"Phantom"}"""

        // Act
        val horse = Horse.fromJson(jsonString)

        // Assert
        assertEquals(6, horse.id)
        assertEquals("Phantom", horse.name)
        assertNull(horse.color)
    }

    @Test
    @DisplayName("Test serialization round trip")
    fun testSerializationRoundTrip() {
        // Arrange
        val original = Horse(id = 7, name = "Tornado", color = "gray")

        // Act
        val jsonString = original.toJson()
        val deserialized = Horse.fromJson(jsonString)

        // Assert
        assertEquals(original, deserialized)
    }

    @Test
    @DisplayName("Test data class equals and hashCode")
    fun testEqualsAndHashCode() {
        // Arrange
        val horse1 = Horse(id = 8, name = "Blaze", color = "chestnut")
        val horse2 = Horse(id = 8, name = "Blaze", color = "chestnut")
        val horse3 = Horse(id = 9, name = "Blaze", color = "chestnut")

        // Assert
        assertEquals(horse1, horse2) // Same values should be equal
        assertNotEquals(horse1, horse3) // Different values should not be equal
        assertEquals(horse1.hashCode(), horse2.hashCode()) // Equal objects should have equal hash codes
    }

    @Test
    @DisplayName("Test data class copy")
    fun testCopy() {
        // Arrange
        val original = Horse(id = 10, name = "Star", color = "palomino")

        // Act
        val copy1 = original.copy()
        val copy2 = original.copy(name = "SuperStar")
        val copy3 = original.copy(id = 11, color = "black")

        // Assert
        assertEquals(original, copy1)
        assertEquals("SuperStar", copy2.name)
        assertEquals(original.id, copy2.id)
        assertEquals(original.color, copy2.color)
        assertEquals(11, copy3.id)
        assertEquals(original.name, copy3.name)
        assertEquals("black", copy3.color)
    }

    @Test
    @DisplayName("Test toString method")
    fun testToString() {
        // Arrange
        val horse = Horse(id = 12, name = "Comet", color = "dapple gray")

        // Act
        val stringRepresentation = horse.toString()

        // Assert
        assertTrue(stringRepresentation.contains("id=12"))
        assertTrue(stringRepresentation.contains("name=Comet"))
        assertTrue(stringRepresentation.contains("color=dapple gray"))
    }

    @Test
    @DisplayName("Test component functions and destructuring")
    fun testComponentFunctions() {
        // Arrange
        val horse = Horse(id = 13, name = "Eclipse", color = "black")

        // Act & Assert - Component functions
        assertEquals(13, horse.component1())
        assertEquals("Eclipse", horse.component2())
        assertEquals("black", horse.component3())

        // Act & Assert - Destructuring
        val (id, name, color) = horse
        assertEquals(13, id)
        assertEquals("Eclipse", name)
        assertEquals("black", color)
    }

    @Test
    @DisplayName("Test invalid JSON handling")
    fun testInvalidJson() {
        // Arrange
        val invalidJson = """{"id":"not-a-number","name":true}"""

        // Act & Assert
        assertThrows(SerializationException::class.java) {
            Horse.fromJson(invalidJson)
        }
    }

    @Test
    @DisplayName("Test with extreme values")
    fun testExtremeValues() {
        // Arrange
        val maxIdHorse = Horse(id = Int.MAX_VALUE, name = "Max", color = "red")
        val longNameHorse = Horse(id = 14, name = "a".repeat(1000), color = "blue")
        val longColorHorse = Horse(id = 15, name = "Rainbow", color = "c".repeat(1000))

        // Act & Assert
        assertEquals(Int.MAX_VALUE, maxIdHorse.id)
        assertEquals(1000, longNameHorse.name.length)
        assertEquals(1000, longColorHorse.color?.length)

        // Test serialization and deserialization of extreme values
        val maxIdDeserialized = Horse.fromJson(maxIdHorse.toJson())
        assertEquals(maxIdHorse, maxIdDeserialized)

        val longNameDeserialized = Horse.fromJson(longNameHorse.toJson())
        assertEquals(longNameHorse, longNameDeserialized)

        val longColorDeserialized = Horse.fromJson(longColorHorse.toJson())
        assertEquals(longColorHorse, longColorDeserialized)
    }
}