package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.api.LotteryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class LotteryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockApi: LotteryApi = mock()
    private lateinit var viewModel: LotteryViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LotteryViewModel(mockApi)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Tests initial lottery pool loading on ViewModel creation
    @Test
    fun initialPoolLoading() = runTest {
        whenever(mockApi.getCurrentAmount()).thenReturn(Response.success(10000))

        val vm = LotteryViewModel(mockApi)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(10000, vm.currentAmount.value)
        assertFalse(vm.isLoading.value)
    }

    // Tests payment flow with amount update and notification
    @Test
    fun paymentUpdatesAmount() = runTest {
        whenever(mockApi.getCurrentAmount()).thenReturn(Response.success(10000))

        whenever(mockApi.payToLottery(
            anyString(),
            anyInt(),
            anyString()
        )).thenReturn(Response.success(
            LotteryApi.PaymentResponse(
                success = true,
                newAmount = 15000,
                message = "Payment successful"
            )
        ))

        val viewModel = LotteryViewModel(mockApi)
        advanceUntilIdle()

        viewModel.payToLottery("player1", 5000, "Test payment")
        advanceUntilIdle()

        assertEquals(15000, viewModel.currentAmount.value)
        assertEquals("Test payment - 5000 € added to lottery", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)

        verify(mockApi).payToLottery(
            "player1",
            5000,
            "Test payment"
        )
    }

    // Tests winning lottery claim with proper amount distribution
    @Test
    fun claimWithWin() = runTest {
        val claimResponse = LotteryApi.ClaimResponse(
            wonAmount = 20000,
            newAmount = 0,
            message = "Payout successful"
        )

        whenever(mockApi.getCurrentAmount())
            .thenReturn(Response.success(25000))
            .thenReturn(Response.success(25000))

        whenever(mockApi.claimLottery(eq("player123")))
            .thenReturn(Response.success(claimResponse))

        val testViewModel = LotteryViewModel(mockApi)
        advanceUntilIdle()

        testViewModel.claimLottery("player123")
        advanceUntilIdle()

        assertEquals(0, testViewModel.currentAmount.value)
        assertEquals("You won 20000 €!", testViewModel.notification.value)
        assertFalse(testViewModel.isLoading.value)

        verify(mockApi, times(2)).getCurrentAmount()
        verify(mockApi).claimLottery("player123")
        verifyNoMoreInteractions(mockApi)
    }

    // Tests empty pool scenario with automatic 50k refill
    @Test
    fun claimEmptyPool() = runTest {
        whenever(mockApi.claimLottery(anyString()))
            .thenReturn(Response.success(
                LotteryApi.ClaimResponse(
                    wonAmount = 0,
                    newAmount = 50000,
                    message = "Pool was empty"
                )
            ))

        whenever(mockApi.getCurrentAmount()).thenReturn(Response.success(0))
        val viewModel = LotteryViewModel(mockApi)
        advanceUntilIdle()

        viewModel.claimLottery("player1")
        advanceUntilIdle()

        assertEquals(50000, viewModel.currentAmount.value)
        assertEquals("Lottery was empty! 50,000 € added to pool", viewModel.notification.value)
        assertFalse(viewModel.isLoading.value)

        verify(mockApi).claimLottery("player1")
    }

}