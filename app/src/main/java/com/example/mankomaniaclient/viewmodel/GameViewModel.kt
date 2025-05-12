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
import com.example.mankomaniaclient.api.StompManager
import com.example.mankomaniaclient.model.DiceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for dice roll mechanics and UI state.
 * Delegates roll requests to StompManager, holds the latest dice result,
 * and tracks whether it's the player's turn.
 */
class GameViewModel : ViewModel() {

    // Holds the most recent dice roll result
    private val _diceResult = MutableStateFlow<DiceResult?>(null)
    val diceResult: StateFlow<DiceResult?> = _diceResult

    // Holds the player's turn state
    private val _isPlayerTurn = MutableStateFlow(true) // Change to false later when full turn system is integrated
    val isPlayerTurn: StateFlow<Boolean> = _isPlayerTurn

    /**
     * Sends a dice roll request to the backend via StompManager.
     * Only works when it's the player's turn.
     */
    fun rollDice(playerId: String) {
        if (!_isPlayerTurn.value) return

        println("ROLL REQUEST for $playerId triggered from UI")
        StompManager.sendRollRequest(playerId)

        // Optionally: disable further rolls until turn is over
        _isPlayerTurn.value = false
    }

    /**
     * Receives and stores the dice result coming from the backend.
     * @param result the result of the dice roll received via WebSocket
     */
    fun receiveDiceResult(result: DiceResult) {
        _diceResult.value = result
    }

    /**
     * Allows manual or backend-triggered control over player's turn.
     * @param isTurn true if it's this player's turn
     */
    fun setPlayerTurn(isTurn: Boolean) {
        _isPlayerTurn.value = isTurn
    }
}