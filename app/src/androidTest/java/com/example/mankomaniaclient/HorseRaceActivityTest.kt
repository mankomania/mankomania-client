package com.example.mankomaniaclient

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.mankomaniaclient.ui.screens.HorseRaceScreen
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel
import org.junit.Rule
import org.junit.Test

class HorseRaceActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun activityLaunchesSuccessfully() {
        // Create a viewModel instance for testing
        val viewModel = HorseRaceViewModel()

        composeTestRule.setContent {
            HorseRaceScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Horse Race").assertIsDisplayed()
        composeTestRule.onNodeWithText("Spin Roulette").assertIsDisplayed()
    }
}