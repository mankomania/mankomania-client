/**
 * @file GameBoardScreen.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description Lays the board cells around the edges (clockwise)
 *              and paints a neutral surface-variant as backdrop.
 **/
package com.example.mankomaniaclient.ui.screens

import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.text.font.FontWeight
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

    // Debug log board size
    Log.d("GameBoardScreen", "Board size=${board.size}")

    // Fallback if board is empty
    if (board.isEmpty()) {
        Text("No cells received! Gameboard is not building. Error!")
        return
    }

    val sideCount = board.size / 4
    val bgColor = MaterialTheme.colorScheme.surfaceVariant

    // helper function: to get player color
    fun getPlayerColor(playerIndex: Int): Color {
        return when (playerIndex) {
            0 -> Color.Red
            1 -> Color.Blue
            2 -> Color.Green
            3 -> Color.Yellow
            else -> Color.Gray
        }
    }

    // helper function: to get player name at position
    fun getPlayerNameAtPosition(position: Int): Pair<String, Color>? {
        players.forEachIndexed { index, player ->
            if (player.position == position) {
                val playerName = playerNames.getOrNull(index) ?: player.name
                return Pair(playerName, getPlayerColor(index))
            }
        }
        return null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(32.dp) // Increased padding for better spacing
    ) {

        /* ---------- Top row ------------------------------------------------ */
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp) // Add some top padding
        ) {
            for (i in 0 until sideCount) {
                Log.d("GameBoardScreen", "Top cell #$i → ${board.getOrNull(i)}")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Player name above the cell if player is on this position
                    val playerInfo = getPlayerNameAtPosition(i)
                    if (playerInfo != null) {
                        Text(
                            text = playerInfo.first,
                            color = playerInfo.second,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(20.dp)) // Maintain spacing
                    }

                    Box {
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

        /* ---------- Right column ------------------------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp) // Add some end padding
        ) {
            for (i in sideCount until 2 * sideCount) {
                Log.d("GameBoardScreen", "Right cell #$i → ${board.getOrNull(i)}")

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        BoardCellView(cell = board[i], players = players)
                        players.forEachIndexed { index, player ->
                            if (player.position == i) {
                                PlayerCharacterView(playerIndex = index)
                            }
                        }
                    }

                    // Player name to the right of the cell if player is on this position
                    val playerInfo = getPlayerNameAtPosition(i)
                    if (playerInfo != null) {
                        Text(
                            text = playerInfo.first,
                            color = playerInfo.second,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(60.dp)) // Maintain spacing
                    }
                }
            }
        }

        /* ---------- Bottom row (reverse) ----------------------------------- */
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp) // Add some bottom padding
        ) {
            for (i in (2 * sideCount until 3 * sideCount).reversed()) {
                Log.d("GameBoardScreen", "Bottom cell #$i → ${board.getOrNull(i)}")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        BoardCellView(cell = board[i], players = players)
                        players.forEachIndexed { index, player ->
                            if (player.position == i) {
                                PlayerCharacterView(playerIndex = index)
                            }
                        }
                    }

                    // Player name below the cell if player is on this position
                    val playerInfo = getPlayerNameAtPosition(i)
                    if (playerInfo != null) {
                        Text(
                            text = playerInfo.first,
                            color = playerInfo.second,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(20.dp)) // Maintain spacing
                    }
                }
            }
        }

        /* ---------- Left column (reverse) ---------------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp) // Add some start padding
        ) {
            for (i in (3 * sideCount until board.size).reversed()) {
                Log.d("GameBoardScreen", "Left cell #$i → ${board.getOrNull(i)}")

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player name to the left of the cell if player is on this position
                    val playerInfo = getPlayerNameAtPosition(i)
                    if (playerInfo != null) {
                        Text(
                            text = playerInfo.first,
                            color = playerInfo.second,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(60.dp)) // Maintain spacing
                    }

                    Box {
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

        // Game info in center (optional - you can remove this if not needed)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mankomania",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Lobby: $lobbyId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}


