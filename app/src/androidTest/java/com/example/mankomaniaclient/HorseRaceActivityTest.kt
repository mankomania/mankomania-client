package com.example.mankomaniaclient

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class HorseRaceActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun activityLaunchesSuccessfully() {
        composeTestRule.setContent {
            // Here you set your HorseRaceScreen or Compose UI content directly,
            // e.g. HorseRaceScreen() or whatever composable shows "Horse Race" and "Spin Roulette"
        }

        // Check that "Horse Race" text is displayed
        composeTestRule.onNodeWithText("Horse Race").assertIsDisplayed()

        // Check that "Spin Roulette" button is displayed
        composeTestRule.onNodeWithText("Spin Roulette").assertIsDisplayed()
    }
}