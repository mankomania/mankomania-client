package com.example.mankomaniaclient.ui.components

import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LotteryContentTest {

    private lateinit var viewModel: LotteryViewModel

    @BeforeEach
    fun setUp() {
        viewModel = mockk(relaxed = true)
    }

    @Test
    fun testPay5kButtonTriggersPayAction() {
        val amount = 5000
        val onPayClick: (Int) -> Unit = { viewModel.payToLottery("player1", amount, "Test reason") }
        onPayClick(amount)
        verify { viewModel.payToLottery("player1", amount, "Test reason") }
    }

    @Test
    fun testClaimButtonTriggersClaimAction() {
        val onClaimClick: () -> Unit = { viewModel.claimLottery("player1") }
        onClaimClick()
        verify { viewModel.claimLottery("player1") }
    }

    @Test
    fun testPayButtonWithZeroAmount() {
        val onPayClick: (Int) -> Unit = { viewModel.payToLottery("player1", 0, "Test reason") }
        onPayClick(0)
        verify(exactly = 0) { viewModel.payToLottery(any(), any(), any()) }
        assert(viewModel.notification.value == "Player has no money left, you have won!")
    }
}
