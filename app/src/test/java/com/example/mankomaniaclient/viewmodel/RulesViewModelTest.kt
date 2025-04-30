package com.example.mankomaniaclient.viewmodel

import com.example.mankomaniaclient.api.GameRulesApi
import com.example.mankomaniaclient.model.GameRules
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
@OptIn(ExperimentalCoroutinesApi::class)
class RulesViewModelTest {

    private lateinit var viewModel: RulesViewModel
    private lateinit var mockApi: GameRulesApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApi = mockk(relaxed = true)
        viewModel = RulesViewModel(mockApi)
    }

    @AfterEach
    fun cleanup() {
        Dispatchers.resetMain()
    }

    // Verifies the ViewModel starts in Loading state
    @Test
    fun initialStateIsLoading() {
        assertTrue(viewModel.uiState.value is RulesUiState.Loading)
    }

    // Tests successful API response handling
    @Test
    fun successfulApiCallUpdatesStateToSuccess() = runTest {
        val mockRules = GameRules(
            title = "Test Rules",
            sections = listOf(
                GameRules.RuleSection("Section 1", "Content 1"),
                GameRules.RuleSection("Section 2", "Content 2")
            )
        )

        coEvery { mockApi.getGameRules() } returns Result.success(mockRules)

        viewModel.fetchGameRules()

        val state = viewModel.uiState.value
        assertTrue(state is RulesUiState.Success)
        assertEquals(mockRules, (state as RulesUiState.Success).rules)
    }

    // Tests error handling with error message
    @Test
    fun failedApiCallUpdatesStateToError() = runTest {
        val errorMessage = "Network error"
        coEvery { mockApi.getGameRules() } returns Result.failure(Exception(errorMessage))

        viewModel.fetchGameRules()

        val state = viewModel.uiState.value
        assertTrue(state is RulesUiState.Error)
        assertEquals(errorMessage, (state as RulesUiState.Error).message)
    }

    // Tests default error message when no message provided
    @Test
    fun failedApiCallWithoutMessageShowsDefaultError() = runTest {
        coEvery { mockApi.getGameRules() } returns Result.failure(Exception())

        viewModel.fetchGameRules()

        val state = viewModel.uiState.value
        assertTrue(state is RulesUiState.Error)
        assertEquals("Unbekannter Fehler", (state as RulesUiState.Error).message)
    }
}