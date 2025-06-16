/**
 * @file StatusBarViewTest.kt
 * @author eles17
 * @since 2025-05-19
 * @description Verifies the player status update logic for StatusBarView using GameViewModel.
 */

package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.model.PlayerStatus
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StatusBarViewTest {

    @Test
    fun `updatePlayerStatus stores correct player status`() = runTest {
        val viewModel = GameViewModel()
        val testStatus = PlayerStatus(
            name = "Lev",
            position = 4,
            balance = 125000,
            money = mapOf(5000 to 2, 10000 to 1),
            isTurn = true
        )

        viewModel.updatePlayerStatus(testStatus)

        val result = viewModel.playerStatuses.value["Lev"]

        assertEquals("Lev", result?.name)
        assertEquals(4, result?.position)
        assertEquals(125000, result?.balance)
        assertEquals(mapOf(5000 to 2, 10000 to 1), result?.money)
        assertEquals(true, result?.isTurn)
    }


    @Test
    fun `statusBarView does not crash on unknown player`() = runTest {
        val viewModel = GameViewModel()
        // Do not add any player status
        val result = viewModel.playerStatuses.value["Ghost"]
        assertEquals(null, result)
    }
}