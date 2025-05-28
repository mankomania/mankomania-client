package com.example.mankomaniaclient

import com.example.mankomaniaclient.ui.screens.GameBoardScreen
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.mankomaniaclient.model.MoveResult
import com.example.mankomaniaclient.viewmodel.GameViewModel
import org.junit.Rule
import org.junit.Test

/**
 * @file GameBoardScreenTest.kt
 * @author eles17
 * @since 2025-05-14
 * @description Tests that the field dialog is shown when MoveResult is received.
 */
class GameBoardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun moveResultDialog_shouldDisplayFieldInfo_whenMoveResultIsPresent() {
        val viewModel = GameViewModel()
        val moveResult = MoveResult(
            newPosition = 0,
            oldPosition = 0,
            fieldType = "Casino",
            fieldDescription = "Try your luck",
            playersOnField = listOf("Anna", "Toni")
        )
        viewModel.onPlayerMoved(moveResult)

        composeTestRule.setContent {
            GameBoardScreen(
                lobbyId = "GAME123",
                playerNames = listOf("Lev", "Anna", "Toni", "Jorge"),
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("You landed on: Casino").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try your luck").assertIsDisplayed()
        composeTestRule.onNodeWithText("Other players here: Anna, Toni").assertIsDisplayed()
    }


    /**
     * Test that no dialog is shown when moveResult is null.
     */
    @Test
    fun dialog_shouldNotAppear_whenMoveResultIsNull() {
        val viewModel = GameViewModel()
        composeTestRule.setContent {
            GameBoardScreen(
                lobbyId = "GAME123",
                playerNames = listOf("Lev", "Anna", "Toni", "Jorge"),
                viewModel = viewModel
            )
        }
        composeTestRule.onNodeWithText("You landed on:").assertDoesNotExist()
    }

    /**
     * Test that player figure appears when a player's position matches a cell.
     */
    @Test
    fun playerCharacter_shouldRender_whenPositionMatches() {
        val viewModel = GameViewModel()
        // Simulate a board and single player at position 0
        viewModel.onGameState(
            com.example.mankomaniaclient.network.GameStateDto(
                board = List(16) { index -> com.example.mankomaniaclient.network.CellDto(index = index, hasBranch = false) },
                players = listOf(com.example.mankomaniaclient.network.PlayerDto(name = "Lev", position = 0))
            )
        )
        composeTestRule.setContent {
            GameBoardScreen(
                lobbyId = "GAME123",
                playerNames = listOf("Lev"),
                viewModel = viewModel
            )
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    /**
     * Test that multiple players on the same cell both render.
     */
    @Test
    fun multiplePlayerCharacters_shouldRender_whenOnSameCell() {
        val viewModel = GameViewModel()
        viewModel.onGameState(
            com.example.mankomaniaclient.network.GameStateDto(
                board = List(16) { index -> com.example.mankomaniaclient.network.CellDto(index = index, hasBranch = false) },
                players = listOf(
                    com.example.mankomaniaclient.network.PlayerDto(name = "Lev", position = 0),
                    com.example.mankomaniaclient.network.PlayerDto(name = "Anna", position = 0)
                )
            )
        )
        composeTestRule.setContent {
            GameBoardScreen(
                lobbyId = "GAME123",
                playerNames = listOf("Lev", "Anna"),
                viewModel = viewModel
            )
        }
        composeTestRule.onAllNodesWithTag("PlayerFigure").assertCountEquals(2)
    }
}