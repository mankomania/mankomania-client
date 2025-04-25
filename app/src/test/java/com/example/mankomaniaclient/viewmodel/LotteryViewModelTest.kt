package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.api.LotteryApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LotteryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LotteryViewModel
    private val mockLotteryApi = mockk<LotteryApi>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LotteryViewModel(mockLotteryApi)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // erfolgreicher Abruf des Lotteriebetrags
    @Test
    fun refreshAmount_success_case() = runTest {
        val expectedAmount = 1000
        coEvery { mockLotteryApi.getCurrentAmount() } returns expectedAmount

        viewModel.refreshAmount()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(expectedAmount, viewModel.currentAmount.value)
        assertFalse(viewModel.isLoading.value)
    }

    // Fehlerfall beim Abruf
    @Test
    fun refreshAmount_failure_case() = runTest {
        coEvery { mockLotteryApi.getCurrentAmount() } returns null

        viewModel.refreshAmount()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Couldn't load lottery amount", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)
    }

    // erfolgreiche Einzahlung
    @Test
    fun payToLottery_success_case() = runTest {
        val playerId = "player1"
        val amount = 100
        val reason = "Test payment"
        val newAmount = 1100
        val successResponse = LotteryApi.PaymentResponse(true, newAmount, "Success")

        coEvery { mockLotteryApi.payToLottery(playerId, amount, reason) } returns successResponse

        viewModel.payToLottery(playerId, amount, reason)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newAmount, viewModel.currentAmount.value)
        assertEquals("$reason - $amount € added to lottery", viewModel.notification.value)
        assertTrue(viewModel.paymentAnimation.value)
        assertFalse(viewModel.isLoading.value)
    }

    // fehlgeschlagene Einzahlung
    @Test
    fun payToLottery_failure_case() = runTest {
        val playerId = "player1"
        val amount = 100
        val reason = "Test payment"
        val errorMessage = "Payment failed"
        val errorResponse = LotteryApi.PaymentResponse(false, 0, errorMessage)

        coEvery { mockLotteryApi.payToLottery(playerId, amount, reason) } returns errorResponse

        viewModel.payToLottery(playerId, amount, reason)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(errorMessage, viewModel.notification.value)
        assertFalse(viewModel.paymentAnimation.value)
        assertFalse(viewModel.isLoading.value)
    }

    // erfolgreicher Gewinnanspruch
    @Test
    fun claimLottery_win_case() = runTest {
        val playerId = "player1"
        val wonAmount = 5000
        val newAmount = 50000
        val successResponse = LotteryApi.ClaimResponse(wonAmount, newAmount, "You won!")

        coEvery { mockLotteryApi.claimLottery(playerId) } returns successResponse

        viewModel.claimLottery(playerId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newAmount, viewModel.currentAmount.value)
        assertEquals("You won $wonAmount €!", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)
    }

    // Ansprüche bei leerer Lotterie
    @Test
    fun claimLottery_empty_case() = runTest {
        val playerId = "player1"
        val wonAmount = 0
        val newAmount = 50000
        val successResponse = LotteryApi.ClaimResponse(wonAmount, newAmount, "Lottery was empty")

        coEvery { mockLotteryApi.claimLottery(playerId) } returns successResponse

        viewModel.claimLottery(playerId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newAmount, viewModel.currentAmount.value)
        assertEquals("Lottery was empty! 50000 € added to pool", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)
    }

    // Fehler bei Ansprüchen
    @Test
    fun claimLottery_failure_case() = runTest {
        val playerId = "player1"
        coEvery { mockLotteryApi.claimLottery(playerId) } returns null

        viewModel.claimLottery(playerId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Claim failed", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)
    }

    // Zusätzlicher Test für Betragsaktualisierung
    @Test
    fun lotteryAmount_updated_in_ViewModel() = runTest {
        coEvery { mockLotteryApi.getCurrentAmount() } returns 12345

        viewModel.refreshAmount()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(12345, viewModel.currentAmount.value)
    }

    // Zusätzlicher Test für Fehlermeldungen
    @Test
    fun notification_shown_failure() = runTest {
        coEvery { mockLotteryApi.getCurrentAmount() } returns null

        viewModel.refreshAmount()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Couldn't load lottery amount", viewModel.notification.value)
    }

    // Test wo die API null zurückgibt
    @Test
    fun payToLottery_nullResponse_case() = runTest {
        val playerId = "player1"
        val amount = 100
        val reason = "Test payment"

        coEvery { mockLotteryApi.payToLottery(playerId, amount, reason) } returns null

        viewModel.payToLottery(playerId, amount, reason)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Payment failed", viewModel.notification.value)
        assertFalse(viewModel.paymentAnimation.value)
        assertFalse(viewModel.isLoading.value)
    }
}