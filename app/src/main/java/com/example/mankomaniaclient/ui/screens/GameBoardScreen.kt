/**
 * @file GameBoardScreen.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description Lays the board cells around the edges (clockwise)
 *              and paints a neutral surface-variant as backdrop.
 **/
package com.example.mankomaniaclient.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.ui.components.BoardCellView
import com.example.mankomaniaclient.viewmodel.GameViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.mankomaniaclient.ui.components.PlayerCharacterView

@Composable
fun GameBoardScreen(lobbyId: String, playerNames: List<String>,viewModel: GameViewModel) {

    // Subscribe to lobby updates when the screen is first displayed
    LaunchedEffect(lobbyId) {
        Log.d("GameBoardScreen", "Subscribing to lobby $lobbyId")
        viewModel.subscribeToLobby(lobbyId)
    }
    // Collect state values
    val board by viewModel.board.collectAsState()
    val players by viewModel.players.collectAsState()
    val moveResult by viewModel.moveResult.collectAsState()
    val lotteryResult by viewModel.lotteryResult.collectAsState()
    val showLotteryDialog by viewModel.showLotteryDialog.collectAsState()
    var showDialog by remember { mutableStateOf(true) }

    // Show move result dialog when a move occurs
    if (moveResult != null && showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("You landed on: ${moveResult!!.fieldType}") },
            text = {
                Column {
                    Text(moveResult!!.fieldDescription)
                    if (moveResult!!.playersOnField.isNotEmpty()) {
                        Text("Other players here: ${moveResult!!.playersOnField.joinToString()}")
                    }
                }
            }
        )
    }

    // Show brief lottery notification (auto-dismisses)
    if (showLotteryDialog && lotteryResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLotteryDialog() },
            confirmButton = {
                Button(onClick = { viewModel.dismissLotteryDialog() }) {
                    Text("OK")
                }
            },
            title = {
                Text(
                    if (lotteryResult!!.wonAmount > 0) "🎰 LOTTERY WIN! 🎰"
                    else "🎰 LOTTERY 🎰"
                )
            },
            text = {
                Column {
                    Text(lotteryResult!!.message)
                    if (lotteryResult!!.wonAmount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Amount won: ${lotteryResult!!.wonAmount} €",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Current pool: ${lotteryResult!!.newPoolAmount} €",
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("(Auto-closing in 3 seconds...)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
        )
    }

    // Debug log board size
    Log.d("GameBoardScreen", "Board size=${board.size}")

    // Fallback if board is empty
    if (board.isEmpty()) {
        Text("No cells received!")
        return
    }

    val sideCount = board.size / 4
    val bgColor = MaterialTheme.colorScheme.surfaceVariant   // << soft grey / blue by default

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
            // Display player names at corners
            text = playerNames.getOrNull(0) ?: "",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(1) ?: "",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(2) ?: "",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(3) ?: "",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
        /* ---------- Top row ------------------------------------------------ */
        Row(Modifier.align(Alignment.TopCenter)) {
            for (i in 0 until sideCount) {
                Log.d("GameBoardScreen", "Top cell #$i → ${board.getOrNull(i)}")

                Box { // Render the top row of the board and display all player figures whose position matches the cell index
                    BoardCellView(cell = board[i], players = players)
                    players.forEachIndexed { index, player ->
                        if (player.position == i) {
                            PlayerCharacterView(playerIndex = index)
                        }
                    }
                }
            }
        }

        /* ---------- Right column ------------------------------------------- */
        Column(Modifier.align(Alignment.CenterEnd)) {
            for (i in sideCount until 2 * sideCount) {
                Log.d("GameBoardScreen", "Right cell #$i → ${board.getOrNull(i)}")

                Box { // Render the right column and show all player figures for each cell where a player's position matches
                    BoardCellView(cell = board[i], players = players)
                    players.forEachIndexed { index, player ->
                        if (player.position == i) {
                            PlayerCharacterView(playerIndex = index)
                        }
                    }
                }
            }
        }

        /* ---------- Bottom row (reverse) ----------------------------------- */
        Row(Modifier.align(Alignment.BottomCenter)) {
            for (i in (2 * sideCount until 3 * sideCount).reversed()) {
                Log.d("GameBoardScreen", "Bottom cell #$i → ${board.getOrNull(i)}")

                Box { // Render the bottom row in reverse and draw all player figures that are on each respective field
                    BoardCellView(cell = board[i], players = players)
                    players.forEachIndexed { index, player ->
                        if (player.position == i) {
                            PlayerCharacterView(playerIndex = index)
                        }
                    }
                }
            }
        }

        /* ---------- Left column (reverse) ---------------------------------- */
        Column(Modifier.align(Alignment.CenterStart)) {
            for (i in (3 * sideCount until board.size).reversed()) {
                Log.d("GameBoardScreen", "Left cell #$i → ${board.getOrNull(i)}")

                Box { // Render the left column in reverse and place player figures on the matching board cells
                    BoardCellView(cell = board[i], players = players)
                    players.forEachIndexed { index, player ->
                        if (player.position == i) {
                            PlayerCharacterView(playerIndex = index)
                        }
                    }
                }
            }
        }
    }
}



