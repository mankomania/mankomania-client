package com.example.mankomaniaclient

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class StartingMoneyScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartingMoneyActivity>()

    @Test
    fun startingMoneyScreen_displaysPlayerId_andMoneyInfo() {
        val testPlayerId = "test-player-123"

        // Set the intent BEFORE the activity is created
        composeTestRule.activityRule.scenario.close()

        val scenario = androidx.test.core.app.ActivityScenario.launch<StartingMoneyActivity>(
            Intent(
                androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                StartingMoneyActivity::class.java
            ).apply {
                putExtra("playerId", testPlayerId)
            }
        )

        composeTestRule.waitForIdle()

        // Assert all visible texts
        composeTestRule.onNodeWithText("Player: $testPlayerId").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: €0").assertIsDisplayed()
        composeTestRule.onNodeWithText("€5,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("€10,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("€50,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("€100,000").assertIsDisplayed()

        scenario.close()
    }
}