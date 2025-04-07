package com.example.mankomaniaclient.ui.components

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

class LotteryContentTest {/*
    // JUnit 4 Rule needed for Compose testing
    private val composeTestRule = createComposeRule()

    // JUnit 5 extension wrapper
    @RegisterExtension
    @JvmField
    val rule: TestRule = RuleChain.outerRule(composeTestRule)

    @Test
    fun `shows current amount correctly`() {
        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = LotteryTestTags()
            )
        }

        composeTestRule.onNodeWithTag("amountText").assertExists()
    }

    @Test
    fun `pay buttons trigger callbacks with correct amounts`() {
        var lastAmount = 0

        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = false,
                paymentAnimation = false,
                onPayClick = { amount -> lastAmount = amount },
                onClaimClick = {},
                testTags = LotteryTestTags()
            )
        }

        composeTestRule.onNodeWithTag("pay5kButton").performClick()
        assertEquals(5000, lastAmount)

        composeTestRule.onNodeWithTag("pay10kButton").performClick()
        assertEquals(10000, lastAmount)
    }

    @Test
    fun `shows loading indicator when loading`() {
        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = "",
                isLoading = true,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = LotteryTestTags()
            )
        }

        composeTestRule.onNodeWithTag("loadingIndicator").assertExists()
    }

    @Test
    fun `shows notification when present`() {
        val testMessage = "Test notification"

        composeTestRule.setContent {
            LotteryContent(
                currentAmount = 5000,
                notification = testMessage,
                isLoading = false,
                paymentAnimation = false,
                onPayClick = {},
                onClaimClick = {},
                testTags = LotteryTestTags()
            )
        }

        composeTestRule.onNodeWithTag("notificationText")
            .assertExists()
            .assertTextEquals(testMessage)
    }
*/}