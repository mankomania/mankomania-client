/*package com.example.mankomaniaclient.ui.components

import androidx.compose.ui.test.junit5.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertIsEnabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import de.mannodermaus.junit5.AndroidComposeExtension
import org.junit.jupiter.api.Assertions.*

class LotteryContentTest {

    @RegisterExtension
    val composeTestRule = AndroidComposeExtension.create(createAndroidComposeRule())

    private val testTags = LotteryTestTags()

    @Test
    fun `displays title and amount correctly`() {
        composeTestRule.activity.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = testTags
            )
        }

        composeTestRule.onNodeWithTag(testTags.title)
            .assertIsDisplayed()
            .assertTextEquals("LOTTERY-POOL")

        composeTestRule.onNodeWithTag(testTags.amount)
            .assertTextEquals("5000 €")
    }

    @Test
    fun `buttons trigger callbacks`() {
        var clickedAmount = 0

        composeTestRule.activity.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = { clickedAmount = it },
                onClaimClick = {},
                testTags = testTags
            )
        }

        composeTestRule.onNodeWithTag(testTags.pay5k)
            .performClick()

        assertEquals(5000, clickedAmount)
    }
}*/