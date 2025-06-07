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

@Composable
fun GameBoardScreen(
    lobbyId: String,
    playerNames: List<String>,
    viewModel: GameViewModel
) {
    /* Subscribe exactly once when the screen appears ------------------- */
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lobbyId, lifecycleOwner) {
        Log.d("GameBoardScreen", "Subscribing to lobby $lobbyId")
        viewModel.subscribeToLobby(lobbyId)
    }

    /* Collect state from the ViewModel -------------------------------- */
    val board      by viewModel.board.collectAsState()
    val players    by viewModel.players.collectAsState()
    val moveResult by viewModel.moveResult.collectAsState()     // type: MoveResult?

    /* Delegate rendering to the pure-UI composable -------------------- */
    GameBoardContent(
        board               = board,
        players             = players,
        lobbyId             = lobbyId,
        playerNames         = playerNames,
        moveResult          = moveResult,
        onDismissMoveResult = { viewModel.clearMoveResult() },
        onRollDice = {
            val myPlayerName = playerNames.firstOrNull() ?: return@GameBoardContent
            viewModel.rollDice(myPlayerName)
        }

    )

}
