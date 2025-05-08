package com.example.mankomaniaclient.api

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RaceResultTest {

    @Test
    fun testRaceResultProperties() {
        // Create a test instance with specific values
        val raceResult = RaceResult(
            winningHorseId = 3,
            playerWon = true,
            amountWon = 500
        )

        // Test that properties return the expected values
        assertEquals(3, raceResult.winningHorseId, "WinningHorseId should be 3")
        assertTrue(raceResult.playerWon, "PlayerWon should be true")
        assertEquals(500, raceResult.amountWon, "AmountWon should be 500")
    }

    @Test
    fun testRaceResultCopy() {
        // Create a base instance
        val original = RaceResult(
            winningHorseId = 2,
            playerWon = false,
            amountWon = 0
        )

        // Create a copy with modified properties
        val modified = original.copy(playerWon = true, amountWon = 300)

        // Test that the copied properties match the original
        assertEquals(2, modified.winningHorseId, "WinningHorseId should remain 2")

        // Test that the modified properties are updated
        assertTrue(modified.playerWon, "PlayerWon should be updated to true")
        assertEquals(300, modified.amountWon, "AmountWon should be updated to 300")
    }

    @Test
    fun testRaceResultEquality() {
        // Create two identical RaceResult instances
        val result1 = RaceResult(1, true, 250)
        val result2 = RaceResult(1, true, 250)
        val result3 = RaceResult(2, true, 250)

        // Test equality
        assertEquals(result1, result2, "Identical RaceResults should be equal")

        // Test inequality
        assertFalse(result1 == result3, "RaceResults with different winning horse IDs should not be equal")
    }

    @Test
    fun testToString() {
        // Create a test instance
        val raceResult = RaceResult(4, false, 0)

        // Get the string representation
        val resultString = raceResult.toString()

        // Verify that the string contains all property values
        assertTrue(resultString.contains("winningHorseId=4"), "ToString should include winningHorseId")
        assertTrue(resultString.contains("playerWon=false"), "ToString should include playerWon")
        assertTrue(resultString.contains("amountWon=0"), "ToString should include amountWon")
    }
}