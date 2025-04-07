package com.example.mankomaniaclient.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class LotteryContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTags = LotteryTestTags()

    @Test
    fun `displays all UI elements correctly`() {
        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 10000,
                notification = "Test message",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = testTags
            )
        }

        composeTestRule.onNodeWithTag(testTags.title)
            .assertExists()
            .assertTextEquals("LOTTERY-POOL")

        composeTestRule.onNodeWithTag(testTags.amount)
            .assertTextEquals("10000 €")

        composeTestRule.onNodeWithTag(testTags.pay5k)
            .assertIsEnabled()
            .assertTextEquals("Pay 5.000 €")

        composeTestRule.onNodeWithTag(testTags.notification)
            .assertTextEquals("Test message")
    }

    @Test
    fun `buttons trigger callbacks`() {
        var payAmount = 0
        var claimClicked = false

        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = { payAmount = it },
                onClaimClick = { claimClicked = true },
                testTags = testTags
            )
        }

        composeTestRule.onNodeWithTag(testTags.pay5k).performClick()
        assertEquals(5000, payAmount)

        composeTestRule.onNodeWithTag(testTags.claim).performClick()
        assertTrue(claimClicked)
    }

    @Test
    fun `loading state disables buttons`() {
        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = true,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = testTags
            )
        }

        composeTestRule.onNodeWithTag(testTags.pay5k)
            .assertIsNotEnabled()

        composeTestRule.onNodeWithTag(testTags.claim)
            .assertIsNotEnabled()
    }
}