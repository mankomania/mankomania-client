/**
 * @file GameBoardContent.kt
 * @author Angela Drucks
 * @since 2025-05-27
 * @description Stateless composable that renders the complete board solely
 *              from passed-in data, making it fully preview-able without a
 *              ViewModel or backend connection.
 */


package com.example.mankomaniaclient.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.model.MoveResult
import com.example.mankomaniaclient.network.CellDto


import com.example.mankomaniaclient.network.PlayerDto
import com.example.mankomaniaclient.ui.components.BoardCellView
import com.example.mankomaniaclient.ui.components.PlayerCharacterView

/**
 * Pure-UI version of the game board. Make UI changes here, not in GameBoardScreen!
 *
 * @param board         Immutable list of cells coming from the server.
 * @param players       Current player states.
 * @param lobbyId       ID is shown in the board centre.
 * @param playerNames   Names in the same order as the players list (for display).
 * @param moveResult    Optional last move result to show in a dialog.
 * @param onDismissMove Result callback when dialog is dismissed.
 */
@Composable
fun GameBoardContent(
    board: List<CellDto>,
    players: List<PlayerDto>,
    lobbyId: String,
    playerNames: List<String>,
    moveResult: MoveResult? = null,
    onDismissMoveResult: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(true) }

    /* Move-result dialog --------------------------------------------- */
    if (moveResult != null && showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismissMoveResult()
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDismissMoveResult()
                    }
                ) { Text("OK") }
            },
            title = { Text("You landed on: ${moveResult.fieldType}") },
            text = {
                Column {
                    Text(moveResult.fieldDescription)
                    if (moveResult.playersOnField.isNotEmpty()) {
                        Text("Other players here: ${moveResult.playersOnField.joinToString()}")
                    }
                }
            }
        )
    }


    /* Fallback when the board has not arrived yet ------------------------ */
    if (board.isEmpty()) {
        Text("No cells received! Gameboard is not building. Error!")
        return
    }

    val sideCount = board.size / 4
    val bgColor   = MaterialTheme.colorScheme.surfaceVariant

    /* -------------------------------------------------------------- helpers */
    fun getPlayerColor(playerIndex: Int): Color =
        when (playerIndex) {
            0    -> Color.Red
            1    -> Color.Blue
            2    -> Color.Green
            3    -> Color.Yellow
            else -> Color.Gray
        }

    fun getPlayerNameAtPosition(position: Int): Pair<String, Color>? {
        players.forEachIndexed { index, player ->
            if (player.position == position) {
                val playerName = playerNames.getOrNull(index) ?: player.name
                return Pair(playerName, getPlayerColor(index))
            }
        }
        return null
    }

    /* --------------------------------------------------------------- Layout */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(32.dp)
    ) {

        /* ---------------- Top row --------------------------------------- */
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        ) {
            for (i in 0 until sideCount) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        Text(
                            text = name,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    } ?: Spacer(Modifier.height(20.dp))

                    Box {
                        BoardCellView(board[i], players)
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) PlayerCharacterView(idx)
                        }
                    }
                }
            }
        }

        /* ---------------- Right column ---------------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        ) {
            for (i in sideCount until 2 * sideCount) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        BoardCellView(board[i], players)
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) PlayerCharacterView(idx)
                        }
                    }

                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        Text(
                            text = name,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } ?: Spacer(Modifier.width(60.dp))
                }
            }
        }

        /* ---------------- Bottom row (reverse) -------------------------- */
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            for (i in (2 * sideCount until 3 * sideCount).reversed()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        BoardCellView(board[i], players)
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) PlayerCharacterView(idx)
                        }
                    }

                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        Text(
                            text = name,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } ?: Spacer(Modifier.height(20.dp))
                }
            }
        }

        /* ---------------- Left column (reverse) ------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
        ) {
            for (i in (3 * sideCount until board.size).reversed()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        Text(
                            text = name,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    } ?: Spacer(Modifier.width(60.dp))

                    Box {
                        BoardCellView(board[i], players)
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) PlayerCharacterView(idx)
                        }
                    }
                }
            }
        }

        /* ---------------- Centre label ----------------------------------- */
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

/*------------------------------------------------------ Preview with dummy data */
@Preview(
    name = "GameBoard – Landscape",
    showBackground = true,
    widthDp = 820,
    heightDp = 420
)
@Composable
private fun GameBoardContentPreview() {
    /* Dummy board: 20 cells, every 5th cell has a branch */
    val board = List(20) { idx -> CellDto(index = idx, hasBranch = idx % 5 == 0) }

    /* Dummy players */
    val players = listOf(
        PlayerDto(name = "Kevin", position = 2),
        PlayerDto(name = "Bob",  position = 5),
        PlayerDto(name = "Clara", position = 11)
    )

    MaterialTheme {
        GameBoardContent(
            board = board,
            players = players,
            lobbyId = "preview",
            playerNames = players.map { it.name }
        )
    }
}

