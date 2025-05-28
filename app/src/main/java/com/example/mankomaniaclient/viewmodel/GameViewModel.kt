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
import com.example.mankomaniaclient.api.LotteryApi
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    // --- Lottery State ------------------------------------------------
    private val _lotteryResult = MutableStateFlow<LotteryResult?>(null)
    val lotteryResult: StateFlow<LotteryResult?> = _lotteryResult

    private val _showLotteryDialog = MutableStateFlow(false)
    val showLotteryDialog: StateFlow<Boolean> = _showLotteryDialog

    private val lotteryApi = LotteryApi()

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

        // Check if player landed on lottery field (field 3)
        if (result.newPosition == 3) {
            triggerLottery(getCurrentPlayerName())
        }
    }

    /** Clears the last move-result so the dialog disappears */
    fun clearMoveResult() {
        _moveResult.value = null
    }

    /**
     * Automatically triggers lottery when landing on field 3
     */
    private fun triggerLottery(playerName: String) {
        viewModelScope.launch {
            try {
                // Get current lottery amount
                val currentAmount = lotteryApi.getCurrentAmount() ?: 0

                if (currentAmount > 0) {
                    // Automatically claim lottery
                    val claimResponse = lotteryApi.claimLottery(playerName)
                    if (claimResponse != null) {
                        _lotteryResult.value = LotteryResult(
                            playerName = playerName,
                            wonAmount = claimResponse.wonAmount,
                            newPoolAmount = claimResponse.newAmount,
                            message = if (claimResponse.wonAmount > 0) {
                                "$playerName won ${claimResponse.wonAmount} € from the lottery!"
                            } else {
                                "Lottery was empty! ${claimResponse.newAmount} € added to pool"
                            }
                        )

                        // Log the lottery result for debugging
                        android.util.Log.d("LOTTERY", "Player $playerName: Won ${claimResponse.wonAmount} €")

                    } else {
                        _lotteryResult.value = LotteryResult(
                            playerName = playerName,
                            wonAmount = 0,
                            newPoolAmount = currentAmount,
                            message = "Lottery claim failed for $playerName"
                        )
                    }
                } else {
                    // If lottery is empty, automatically add starter amount
                    val payResponse = lotteryApi.payToLottery(playerName, 1000, "Lottery starter")
                    _lotteryResult.value = LotteryResult(
                        playerName = playerName,
                        wonAmount = 0,
                        newPoolAmount = payResponse?.newAmount ?: 1000,
                        message = "Lottery was empty! $playerName added 1000 € to start the pool"
                    )

                    android.util.Log.d("LOTTERY", "Player $playerName started lottery with 1000 €")
                }

                // Show notification briefly, then auto-dismiss
                _showLotteryDialog.value = true

                // Auto-dismiss after 3 seconds
                kotlinx.coroutines.delay(3000)
                _showLotteryDialog.value = false
                _lotteryResult.value = null

            } catch (e: Exception) {
                android.util.Log.e("LOTTERY", "Lottery error for $playerName: ${e.message}")
                _lotteryResult.value = LotteryResult(
                    playerName = playerName,
                    wonAmount = 0,
                    newPoolAmount = 0,
                    message = "Lottery error for $playerName"
                )

                // Also auto-dismiss error messages
                _showLotteryDialog.value = true
                kotlinx.coroutines.delay(2000)
                _showLotteryDialog.value = false
                _lotteryResult.value = null
            }
        }
    }

    /**
     * Dismisses the lottery dialog
     */
    fun dismissLotteryDialog() {
        _showLotteryDialog.value = false
        _lotteryResult.value = null
    }

    /**
     * Gets the current player's name (you might need to adjust this based on your game logic)
     */
    private fun getCurrentPlayerName(): String {
        // This is a placeholder - you'll need to implement logic to get the current player
        // You might need to track whose turn it is or get it from the move result
        return _players.value.firstOrNull()?.name ?: "Unknown Player"
    }
}

/**
 * Data class for lottery results
 */
data class LotteryResult(
    val playerName: String,
    val wonAmount: Int,
    val newPoolAmount: Int,
    val message: String
)
