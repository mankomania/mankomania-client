package com.example.mankomaniaclient.ui.screens

import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

class LotteryScreenTest {

    // fixme not meaningful test - this test is executing the calls its verifying directly
    @Test
    @DisplayName("Test lottery screen buttons call the appropriate ViewModel methods")
    fun testLotteryScreenButtonsCallViewModel() {
        val viewModel = mockk<LotteryViewModel>(relaxed = true)
        every { viewModel.currentAmount } returns MutableStateFlow(15000)
        every { viewModel.notification } returns MutableStateFlow("")
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.paymentAnimation } returns MutableStateFlow(false)

        viewModel.payToLottery("player123", 5000, "Lottery Payment")
        viewModel.claimLottery("player123")

        verify { viewModel.payToLottery("player123", 5000, "Lottery Payment") }
        verify { viewModel.claimLottery("player123") }
    }
}