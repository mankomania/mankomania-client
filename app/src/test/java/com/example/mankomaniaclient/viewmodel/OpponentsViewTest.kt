/**
 * @file OpponentsViewTest.kt
 * @author eles17
 * @since 2025-05-19
 * @description Verifies that playerStatuses stores multiple players correctly for rendering in OpponentsView.
 */

package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.model.PlayerStatus
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpponentsViewTest {

    @Test
    fun `updatePlayerStatus stores multiple player statuses correctly`() = runTest {
        val viewModel = GameViewModel()
        val player1 = PlayerStatus(
            name = "Lev",
            position = 4,
            balance = 125000,
            money = mapOf(5000 to 2),
            isTurn = true
        )
        val player2 = PlayerStatus(
            name = "Anna",
            position = 6,
            balance = 97000,
            money = mapOf(10000 to 1, 5000 to 3),
            isTurn = false
        )

        viewModel.updatePlayerStatus(player1)
        viewModel.updatePlayerStatus(player2)

        val result = viewModel.playerStatuses.value

        assertEquals(2, result.size)
        assertEquals(4, result["Lev"]?.position)
        assertEquals(97000, result["Anna"]?.balance)
        assertEquals(mapOf(10000 to 1, 5000 to 3), result["Anna"]?.money)
        assertEquals(true, result["Lev"]?.isTurn)
        assertEquals(false, result["Anna"]?.isTurn)
    }
}