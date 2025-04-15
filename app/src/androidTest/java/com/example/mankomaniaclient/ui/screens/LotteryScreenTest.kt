package com.example.mankomaniaclient.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mankomaniaclient.ui.components.LotteryTestTags
import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LotteryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockViewModel: LotteryViewModel = mockk(relaxed = true) {
        every { currentAmount } returns MutableStateFlow(10000)
        every { notification } returns MutableStateFlow("")
        every { isLoading } returns MutableStateFlow(false)
        every { paymentAnimation } returns MutableStateFlow(false)
    }

    @Test
    fun should_display_current_amount() {
        composeTestRule.setContent {
            LotteryScreen(playerId = "test", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(LotteryTestTags.AMOUNT)
            .assertExists()
            .assertTextEquals("10000 €")
    }

    @Test
    fun should_call_pay_when_button_clicked() {
        composeTestRule.setContent {
            LotteryScreen(playerId = "test", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(LotteryTestTags.PAY_5K).performClick()
        verify { mockViewModel.payToLottery("test", 5000, "Lottery Payment") }
    }

    @Test
    fun should_call_claim_when_button_clicked() {
        composeTestRule.setContent {
            LotteryScreen(playerId = "test", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(LotteryTestTags.CLAIM).performClick()
        verify { mockViewModel.claimLottery("test") }
    }
}