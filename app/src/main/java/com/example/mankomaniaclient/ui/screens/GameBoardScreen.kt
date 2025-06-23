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
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mankomaniaclient.network.CellDto
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
    val board         by viewModel.board.collectAsState()
    val players       by viewModel.players.collectAsState()
    val moveResult    by viewModel.moveResult.collectAsState()     // type: MoveResult?
    val selectedCell  by viewModel.selectedCell.collectAsState()

    /* Delegate rendering to the pure-UI composable -------------------- */
    GameBoardContent(
        board               = board,
        players             = players,
        lobbyId             = lobbyId,
        playerNames         = playerNames,
        moveResult          = moveResult,
        onDismissMoveResult = { viewModel.clearMoveResult() },
        onCellClick         = { cell -> viewModel.onCellSelected(cell) }
    )

    /* Shows the dialog with cell details when selected */
    if (selectedCell != null) {
        CellDetailsDialog(
            cell = selectedCell!!,
            onDismiss = { viewModel.clearSelectedCell() }
        )
    }
}

@Composable
fun CellDetailsDialog(
    cell: CellDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cell #${cell.index}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Type: ${cell.type}")
                if (cell.hasBranch) {
                    Text("This cell has a branch")
                }
                // Add more cell information here
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}