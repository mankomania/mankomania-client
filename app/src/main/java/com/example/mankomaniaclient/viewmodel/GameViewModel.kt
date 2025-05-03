package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * # GameViewModel
 *
 * Verwaltet den UI-Zustand und reagiert auf Benutzeraktionen.
 *
 * @author
 * @since
 * @description Dieses ViewModel dient als Bindeglied zwischen UI und Spiel-Logik.
 */
data class DiceResult(val die1: Int, val die2: Int) {
    val sum: Int get() = die1 + die2
}

class GameViewModel : ViewModel() {
    private val _diceResult = MutableStateFlow<DiceResult?>(null)
    val diceResult: StateFlow<DiceResult?> = _diceResult

    fun rollDice(playerId: String){
        //this will be calling the stompManager later
        println("ROLL REQUEST for $playerId triggered from UI")
        // Mock result for UI testing
        _diceResult.value = DiceResult((1..6).random(), (1..6).random())
    }

    fun receiveDiceResult(result:DiceResult){
        _diceResult.value = result
    }
}