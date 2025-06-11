package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mankomaniaclient.ui.components.HorseColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class HorseRaceViewModel : ViewModel() {

    // Holds the current winning horse after a roulette spin
    private val _winner = MutableStateFlow<HorseColor?>(null)
    val winner: StateFlow<HorseColor?> = _winner

    // Holds the horse selected by the player for betting
    val selectedHorse = MutableStateFlow<HorseColor?>(null)

    // Holds the result of the player's bet ("You won!" or "You lost.")
    val betResult = MutableStateFlow<String?>(null)

    /**
     * Spins the roulette by selecting a horse based on weighted probability.
     */
    fun spinRoulette() {
        val weightedList = HorseColor.values().flatMap { color ->
            List(color.weight) { color }
        }
        _winner.value = weightedList.random(Random)
    }

    /**
     * Compares the selected horse with the winning one and updates the result.
     */
    fun placeBet() {
        val bet = selectedHorse.value
        val result = winner.value

        betResult.value = when {
            bet == null -> "Please select a horse to bet on."
            result == null -> "Spin the roulette first!"
            bet == result -> "You won!"
            else -> "You lost. Try again!"
        }
    }
}