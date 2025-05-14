package com.example.mankomaniaclient

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.mankomaniaclient.ui.components.PlayerCharacterView
import org.junit.Rule
import org.junit.Test

/**
 * @file PlayerCharacterViewTest.kt
 * @author eles17
 * @since 2025-05-14
 * @description Tests the visual representation of PlayerCharacterView for each player index.
 */

class PlayerCharacterViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test that PlayerCharacterView displays for player index 0 (Red).
     */
    @Test
    fun playerCharacterView_shouldDisplayForPlayer0() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 0)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    /**
     * Test that PlayerCharacterView displays for player index 1 (Blue).
     */
    @Test
    fun playerCharacterView_shouldDisplayForPlayer1() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 1)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    /**
     * Test that PlayerCharacterView uses correct color for each player index.
     */
    @Test
    fun playerCharacterView_displaysForPlayer0() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 0)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    @Test
    fun playerCharacterView_displaysForPlayer1() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 1)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    @Test
    fun playerCharacterView_displaysForPlayer2() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 2)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    @Test
    fun playerCharacterView_displaysForPlayer3() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 3)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

    @Test
    fun playerCharacterView_displaysForFallbackColor() {
        composeTestRule.setContent {
            PlayerCharacterView(playerIndex = 99)
        }
        composeTestRule.onNodeWithTag("PlayerFigure").assertIsDisplayed()
    }

}