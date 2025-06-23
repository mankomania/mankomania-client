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

class GameViewModel : ViewModel() {

    // --- Board & Players State ----------------------------------------
    private val _board   = MutableStateFlow<List<CellDto>>(emptyList())
    val board: StateFlow<List<CellDto>> = _board

    private val _players = MutableStateFlow<List<PlayerDto>>(emptyList())
    val players: StateFlow<List<PlayerDto>> = _players

    private val _moveResult = MutableStateFlow<MoveResult?>(null)
    val moveResult: StateFlow<MoveResult?> = _moveResult

    private val _myPlayerName = MutableStateFlow("")
    val myPlayerName: StateFlow<String> = _myPlayerName

    fun setMyPlayerName(name: String) {
        println(">>> setMyPlayerName called with: $name")
        _myPlayerName.value = name
    }

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
        val myName = myPlayerName.value
        println(">> onGameState: myName = '$myName' | currentTurnPlayerName = '${state.currentTurnPlayerName}'")

        _board.value   = state.board
        _players.value = state.players
        println("myName: $myName | currentTurnPlayerName: ${state.currentTurnPlayerName}")


        _isPlayerTurn.value = (myName == state.currentTurnPlayerName)
        println(">>> isPlayerTurn wird gesetzt auf ${myName == state.currentTurnPlayerName}")

    }

    // --- Dice Roll State ----------------------------------------------
    private val _diceResult   = MutableStateFlow<DiceResult?>(null)
    val diceResult: StateFlow<DiceResult?> = _diceResult

    private val _isPlayerTurn = MutableStateFlow(false)
    val isPlayerTurn: StateFlow<Boolean> = _isPlayerTurn

    /**
     * Sends a dice roll request to the backend via StompManager.
     * Only works when it's the player's turn.
     */
    fun rollDice(playerName: String) {
        if (!_isPlayerTurn.value) return

        println("ROLL REQUEST for $playerName triggered from UI")
        WebSocketService.send("/app/rollDice", playerName)
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
}