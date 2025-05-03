package com.example.mankomaniaclient.viewmodel
/**
 * @file GameViewModel.kt
 * @author eles17
 * @since 3.5.2025
 * @description
 * Data class representing the result of a dice roll.
 * Holds both dice values and calculates the sum.
 * ----------------------------------------------------------
 * ViewModel for managing dice roll interactions and UI state.
 * Handles triggering of roll requests and updating UI based on results.
 */
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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