/*
 * @file GameViewModel.kt
 * @author Angela Drucks, Lev Starman, Anna-Paloma Walder
 * @since 2025-05-08
 * @description
 * ViewModel that holds the current game state:
 * the list of board cells and the list of players.
 * ViewModel for managing dice roll interactions and UI state.
 * Handles triggering of roll requests and updating UI based on results.
 */

package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mankomaniaclient.network.CellDto
import com.example.mankomaniaclient.network.GameStateDto
import com.example.mankomaniaclient.network.PlayerDto
import com.example.mankomaniaclient.network.WebSocketService
import com.example.mankomaniaclient.model.DiceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    // --- Board & Players State ----------------------------------------
    private val _board   = MutableStateFlow<List<CellDto>>(emptyList())
    val board: StateFlow<List<CellDto>> = _board

    private val _players = MutableStateFlow<List<PlayerDto>>(emptyList())
    val players: StateFlow<List<PlayerDto>> = _players

    /** Called by WebSocketService when a new GameStateDto arrives */
    fun onGameState(state: GameStateDto) {
        _board.value   = state.board
        _players.value = state.players
    }

    // --- Dice Roll State ----------------------------------------------
    private val _diceResult   = MutableStateFlow<DiceResult?>(null)
    val diceResult: StateFlow<DiceResult?> = _diceResult

    private val _isPlayerTurn = MutableStateFlow(true)
    val isPlayerTurn: StateFlow<Boolean> = _isPlayerTurn

    /**
     * Sends a dice roll request to the backend via StompManager.
     * Only works when it's the player's turn.
     */
    fun rollDice(playerId: String) {
        if (!_isPlayerTurn.value) return

        println("ROLL REQUEST for $playerId triggered from UI")
        WebSocketService.send("/app/rollDice", playerId)
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