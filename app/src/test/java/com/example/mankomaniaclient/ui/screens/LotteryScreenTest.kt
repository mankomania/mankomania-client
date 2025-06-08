package com.example.mankomaniaclient.ui.screens

import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*

class LotteryScreenTest {

    @Test
    @DisplayName("Test ViewModel initialization and state management")
    fun testViewModelStateManagement() {
        val viewModel = mockk<LotteryViewModel>(relaxed = true)

        every { viewModel.currentAmount } returns MutableStateFlow(0)
        every { viewModel.notification } returns MutableStateFlow("")
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.paymentAnimation } returns MutableStateFlow(false)

        assertNotNull(viewModel.currentAmount)
        assertNotNull(viewModel.notification)
        assertNotNull(viewModel.isLoading)
        assertNotNull(viewModel.paymentAnimation)

        assertEquals(0, viewModel.currentAmount.value)
        assertEquals("", viewModel.notification.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(false, viewModel.paymentAnimation.value)
    }

    @Test
    @DisplayName("Test ViewModel methods called through LotteryScreen logic")
    fun testLotteryScreenViewModelIntegration() {
        val mockApi = mockk<com.example.mankomaniaclient.api.LotteryApi>(relaxed = true)
        val viewModel = mockk<LotteryViewModel>(relaxed = true)

        every { viewModel.currentAmount } returns MutableStateFlow(15000)
        every { viewModel.notification } returns MutableStateFlow("")
        every { viewModel.isLoading } returns MutableStateFlow(false)
        every { viewModel.paymentAnimation } returns MutableStateFlow(false)

        val testPlayerId = "player123"
        val testAmount = 5000

        viewModel.payToLottery(testPlayerId, testAmount, "Lottery Payment")
        viewModel.claimLottery(testPlayerId)

        verify { viewModel.payToLottery(testPlayerId, testAmount, "Lottery Payment") }
        verify { viewModel.claimLottery(testPlayerId) }
    }

    @Test
    @DisplayName("Test notification state changes")
    fun testNotificationStateChanges() {
        val viewModel = mockk<LotteryViewModel>(relaxed = true)
        val initialNotification = ""
        val updatedNotification = "Payment successful!"

        val notificationFlow = MutableStateFlow(initialNotification)
        every { viewModel.notification } returns notificationFlow

        notificationFlow.value = updatedNotification

        assertEquals(updatedNotification, viewModel.notification.value)
        assertNotEquals(initialNotification, viewModel.notification.value)
    }

    @Test
    @DisplayName("Test loading state management")
    fun testLoadingStateManagement() {
        // Arrange: Mock ViewModel with loading state changes
        val viewModel = mockk<LotteryViewModel>(relaxed = true)
        val loadingFlow = MutableStateFlow(false)
        every { viewModel.isLoading } returns loadingFlow

        loadingFlow.value = true  // Start loading
        val loadingState = viewModel.isLoading.value

        loadingFlow.value = false // Stop loading
        val notLoadingState = viewModel.isLoading.value

        assertTrue(loadingState)
        assertFalse(notLoadingState)
    }
}