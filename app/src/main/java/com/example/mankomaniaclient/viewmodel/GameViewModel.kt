package com.example.mankomaniaclient.viewmodel

/**
 * @file GameViewModel.kt
 * @author eles17
 * @since 3.5.2025
 * @description
 * ViewModel for managing dice roll interactions and UI state.
 * Handles triggering of roll requests and updating UI based on results.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.StompManager
import com.example.mankomaniaclient.model.DiceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for dice roll mechanics and UI state.
 * Delegates roll requests to StompManager and holds the latest dice result.
 */
class GameViewModel : ViewModel() {

    // Holds the most recent dice roll result
    private val _diceResult = MutableStateFlow<DiceResult?>(null)
    val diceResult: StateFlow<DiceResult?> = _diceResult

    /**
     * Sends a dice roll request to the backend via StompManager.
     * @param playerId the identifier of the player who rolled the dice
     */
    fun rollDice(playerId: String) {
        println("ROLL REQUEST for $playerId triggered from UI")
        StompManager.sendRollRequest(playerId)
    }

    /**
     * Receives and stores the dice result coming from the backend.
     * @param result the result of the dice roll received via WebSocket
     */
    fun receiveDiceResult(result: DiceResult) {
        _diceResult.value = result
    }
}