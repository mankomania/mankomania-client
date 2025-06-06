package com.example.mankomaniaclient.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.ui.components.HorseColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class HorseRaceViewModel : ViewModel() {

    private val _winner = MutableStateFlow<HorseColor?>(null)
    val winner: StateFlow<HorseColor?> = _winner

    /**
     * Spins the roulette and selects a winning horse based on weight.
     */
    fun spinRoulette() {
        viewModelScope.launch {
            val weightedList = HorseColor.values().flatMap { color ->
                List(color.weight) { color }
            }
            _winner.value = weightedList.random()
        }
    }

    /**
     * Resets the winner state to allow a new race.
     */
    fun resetRace() {
        _winner.value = null
    }
}