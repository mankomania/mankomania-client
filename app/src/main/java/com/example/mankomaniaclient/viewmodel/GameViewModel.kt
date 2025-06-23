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
import com.example.mankomaniaclient.model.MoveResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    // --- Board & Players State ----------------------------------------
    private val _board   = MutableStateFlow<List<CellDto>>(emptyList())
    val board: StateFlow<List<CellDto>> = _board

    private val _players = MutableStateFlow<List<PlayerDto>>(emptyList())
    val players: StateFlow<List<PlayerDto>> = _players

    private val _moveResult = MutableStateFlow<MoveResult?>(null)
    val moveResult: StateFlow<MoveResult?> = _moveResult

    // --- Selected Cell State ------------------------------------------
    private val _selectedCell = MutableStateFlow<CellDto?>(null)
    val selectedCell = _selectedCell.asStateFlow()

    /**
     * Subscribe to the given lobby via WebSocket.
     * This will route incoming GameStateDto and MoveResults automatically
     */
    fun subscribeToLobby(lobbyId: String) {
        WebSocketService.subscribeToLobby(lobbyId)
    }

    init {
        // Register this ViewModel with the WebSocketService for callbacks
        WebSocketService.setGameViewModel(this)
    }

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

    fun onPlayerMoved(result: MoveResult) {
        _moveResult.value = result
    }

    /** Clears the last move-result so the dialog disappears */
    fun clearMoveResult() {
        _moveResult.value = null
    }

    /**
     * Updates the selected cell state when a cell is clicked
     * @param cell the cell that was clicked
     */
    fun onCellSelected(cell: CellDto) {
        _selectedCell.value = cell
    }

    /**
     * Resets the selected cell state (e.g., when closing a dialog)
     */
    fun clearSelectedCell() {
        _selectedCell.value = null
    }
}