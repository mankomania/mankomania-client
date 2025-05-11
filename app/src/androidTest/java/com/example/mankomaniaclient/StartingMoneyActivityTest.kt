package com.example.mankomaniaclient

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.example.mankomaniaclient.ui.StartingMoneyScreen
import org.junit.Rule
import org.junit.Test

class StartingMoneyActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<StartingMoneyActivity>()

    @Test
    fun startingMoneyScreen_displaysPlayerId() {
        // Launch the activity with a test playerId
        val testPlayerId = "test-player-123"
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val intent = Intent(activity, StartingMoneyActivity::class.java).apply {
                putExtra("playerId", testPlayerId)
            }
            activity.startActivity(intent)
        }

        // Verify that the playerId is displayed on screen
        composeTestRule.onNodeWithText("Player: $testPlayerId").assertIsDisplayed()
    }
}