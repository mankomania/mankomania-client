package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.model.DiceResult
import com.example.mankomaniaclient.model.PlayerStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import app.cash.turbine.test

/**
 * @file GameViewModelTest.kt
 * @description
 * Unit tests for GameViewModel to verify game state logic,
 * dice rolling, and turn handling.
 *
 * @since 13.05.2025
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    /**
     * Test that the initial dice result is null before any roll is made.
     */
    @Test
    fun testInitialDiceResultIsNull() = runTest {
        val viewModel = GameViewModel()
        assertNull(viewModel.diceResult.value)
    }

    /**
     * Test that rollDice generates valid dice values and updates the result.
     */
   /** @Test
    fun testRollDiceGeneratesValidResult() = runTest {
        val viewModel = GameViewModel()
        viewModel.rollDice("testPlayer")

        // Retry mechanism to wait for result up to 10 times
        var result = viewModel.diceResult.value
        repeat(10) {
            if (result != null) return@repeat
            kotlinx.coroutines.delay(10)
            result = viewModel.diceResult.value
        }

        assertNotNull(result, "Dice result should not be null after rolling")
        result?.let {
            assertTrue(it.die1 in 1..6)
            assertTrue(it.die2 in 1..6)
            assertEquals(it.die1 + it.die2, it.sum)
        }
    }**/

    /**
     * Test that setPlayerTurn correctly updates the isPlayerTurn state.
     */
    @Test
    fun testSetPlayerTurnTrue() = runTest {
        val viewModel = GameViewModel()
        viewModel.setPlayerTurn(true)
        assertTrue(viewModel.isPlayerTurn.value)
    }

    /**
     * Test that setPlayerTurn can set the state to false.
     */
    @Test
    fun testSetPlayerTurnFalse() = runTest {
        val viewModel = GameViewModel()
        viewModel.setPlayerTurn(false)
        assertFalse(viewModel.isPlayerTurn.value)
    }

    /**
     * Test that onPlayerMoved updates the moveResult state.
     */
    @Test
    fun testOnPlayerMovedUpdatesState() = runTest {
        val viewModel = GameViewModel()
        val result = com.example.mankomaniaclient.model.MoveResult(
            newPosition = 2,
            oldPosition = 0,
            fieldType = "PayFeeAction",
            fieldDescription = "Pay fee to the bank",
            playersOnField = listOf("Anna")
        )
        viewModel.onPlayerMoved(result)
        assertEquals(result, viewModel.moveResult.value)
    }

    @Test
    fun `updatePlayerStatus should add or replace player status`() = runTest {
        val viewModel = GameViewModel()
        val status1 = PlayerStatus("Toni", position = 3, balance = 50000, money = mapOf(5000 to 5))
        val status2 = PlayerStatus("Jorge", position = 5, balance = 80000, money = mapOf(10000 to 2))

        viewModel.updatePlayerStatus(status1)
        viewModel.updatePlayerStatus(status2)

        viewModel.playerStatuses.test {
            val value = awaitItem()
            assertEquals(2, value.size)
            assertEquals(3, value["Toni"]?.position)
            assertEquals(80000, value["Jorge"]?.balance)
            cancelAndIgnoreRemainingEvents()
        }
    }
}