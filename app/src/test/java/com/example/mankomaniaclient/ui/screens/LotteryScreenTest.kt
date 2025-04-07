package com.example.mankomaniaclient.ui.screens

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mankomaniaclient.MainActivity
import com.example.mankomaniaclient.ui.components.LotteryTestTags
import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LotteryScreenTest {/*
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testTags = LotteryTestTags()

    @Test
    fun `screen reflects viewmodel state`() {
        val mockViewModel = mockk<LotteryViewModel>().apply {
            every { currentAmount } returns MutableStateFlow(7500)
            every { notification } returns MutableStateFlow("Welcome")
            every { isLoading } returns MutableStateFlow(false)
        }

        composeTestRule.setContent {
            LotteryScreen(
                playerId = "test123",
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag(testTags.amount)
            .assertTextEquals("7500 €")

        composeTestRule.onNodeWithTag(testTags.notification)
            .assertTextEquals("Welcome")
    }

    @Test
    fun `screen handles loading state`() {
        val mockViewModel = mockk<LotteryViewModel>().apply {
            every { isLoading } returns MutableStateFlow(true)
        }

        composeTestRule.setContent {
            LotteryScreen(
                playerId = "test123",
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag(testTags.loading)
            .assertExists()
    }*/
}