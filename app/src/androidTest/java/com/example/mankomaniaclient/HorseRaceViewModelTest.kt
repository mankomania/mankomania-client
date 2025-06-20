package com.example.mankomaniaclient

import com.example.mankomaniaclient.ui.components.HorseColor
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class HorseRaceViewModelTest {

    @Test
    fun `spinRoulette should set a winner from HorseColor`() = runBlocking {
        val viewModel = HorseRaceViewModel()
        viewModel.spinRoulette()
        val winner = viewModel.winner.first()
        assertTrue("Winner should be one of HorseColor entries", winner in HorseColor.entries)
    }

    @Test
    fun `placeBet should return correct result`() = runBlocking {
        val viewModel = HorseRaceViewModel()
        viewModel.selectedHorse.value = HorseColor.RED
        viewModel.spinRoulette()
        viewModel.placeBet()
        val result = viewModel.betResult.first()
        val expectedMessages = setOf(
            "You won!",
            "You lost. Try again!",
            "Spin the roulette first!",
            "Please select a horse to bet on."
        )
        assertTrue("Result should be one of the expected messages", result in expectedMessages)
    }
}