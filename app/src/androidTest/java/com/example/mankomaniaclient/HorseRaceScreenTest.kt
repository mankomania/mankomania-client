package com.example.mankomaniaclient

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.mankomaniaclient.ui.screens.HorseRaceScreen
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel
import org.junit.Rule
import org.junit.Test

class HorseRaceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun horseRaceScreen_showsTitleAndButtons() {
        val viewModel = HorseRaceViewModel()
        composeTestRule.setContent {
            HorseRaceScreen(viewModel)
        }

        // Controlla che il titolo esista
        composeTestRule.onNodeWithTag("Title").assertExists()

        // Controlla che i bottoni esistano
        composeTestRule.onNodeWithTag("SpinButton").assertExists()

        // Controlla la presenza di ogni colore tramite testTag
        composeTestRule.onNodeWithTag("HorseColor_RED").assertExists()
        composeTestRule.onNodeWithTag("HorseColor_BLUE").assertExists()
        composeTestRule.onNodeWithTag("HorseColor_YELLOW").assertExists()
        composeTestRule.onNodeWithTag("HorseColor_GREEN").assertExists()
    }
}