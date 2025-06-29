/**
 * @file GameBoardScreen.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description ViewModel-aware wrapper: subscribes to lobby updates and feeds
 *              state into the stateless GameBoardContent composable.
 *              Here no UI  -> for UI go into GameboardContent.kt and use compose preview there.
 */
package com.example.mankomaniaclient.ui.screens

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mankomaniaclient.viewmodel.GameViewModel
import com.example.mankomaniaclient.network.WebSocketService


@Composable
fun GameBoardScreen(
    lobbyId: String,
    playerNames: List<String>,
    viewModel: GameViewModel,
    myPlayerName: String
) {
    /* Subscribe exactly once when the screen appears ------------------- */
    LaunchedEffect(Unit) {
        Log.d("DEBUG", "→ 1. Spielername setzen")
        viewModel.setMyPlayerName(myPlayerName)

        Log.d("DEBUG", "→ 2. ViewModel setzen")
        WebSocketService.setGameViewModel(viewModel)

        WebSocketService.subscribeToPlayerMoved(lobbyId) { moveResult ->
            Log.d("PLAYER-MOVED", "Empfangen in GameBoard: $moveResult")
            viewModel.onPlayerMoved(moveResult)
        }

        Log.d("DEBUG", "→ 3. Lobby abonnieren")
        viewModel.subscribeToLobby(lobbyId)

    }


    /* Collect state from the ViewModel -------------------------------- */
    val board      by viewModel.board.collectAsState()
    val players    by viewModel.players.collectAsState()
    val moveResult by viewModel.moveResult.collectAsState()     // type: MoveResult?
    val isMyTurn by viewModel.isPlayerTurn.collectAsState()
    /* Delegate rendering to the pure-UI composable -------------------- */
    GameBoardContent(
        board               = board,
        players             = players,
        lobbyId             = lobbyId,
        playerNames         = playerNames,
        moveResult          = moveResult,
        onDismissMoveResult = { viewModel.clearMoveResult() },
        onRollDice = {
            viewModel.rollDice(myPlayerName)
        },
        viewModel = viewModel,
    )

}
