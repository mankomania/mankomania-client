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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.model.MoveResult
import com.example.mankomaniaclient.network.CellDto
import com.example.mankomaniaclient.network.PlayerDto
import com.example.mankomaniaclient.ui.components.BoardCellView
import com.example.mankomaniaclient.ui.components.PlayerCharacterView
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    "Du landest auf: ${moveResult.fieldType}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            },
            text = {
                Column {
                    Text(
                        moveResult.fieldDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (moveResult.playersOnField.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Andere Spieler hier: ${moveResult.playersOnField.joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    /* Fallback when the board has not arrived yet ------------------------ */
    if (board.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "🎲 Spielbrett wird geladen...",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF666666)
            )
        }
        return
    }

    val sideCount = board.size / 4

    /* -------------------------------------------------------------- helpers */
    fun getPlayerColor(playerIndex: Int): Color =
        when (playerIndex) {
            0    -> Color(0xFFE53E3E) // Modernes Rot
            1    -> Color(0xFF3182CE) // Modernes Blau
            2    -> Color(0xFF38A169) // Modernes Grün
            3    -> Color(0xFFD69E2E) // Modernes Gelb/Gold
            else -> Color(0xFF718096) // Modernes Grau
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

    @Composable
    fun EnhancedBoardCell(
        cell: CellDto,
        players: List<PlayerDto>,
        modifier: Modifier = Modifier
    ) {
        val isSpecialCell = cell.hasBranch
        val hasPlayer = players.any { it.position == cell.index }

        Box(
            modifier = modifier
                .size(80.dp) // Größere Zellen!
                .shadow(
                    elevation = if (isSpecialCell) 8.dp else 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = if (isSpecialCell) Color(0xFFFFD700) else Color.Black
                )
                .background(
                    brush = if (isSpecialCell) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA000)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF5F5F5)
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = if (hasPlayer) 3.dp else 1.dp,
                    color = if (hasPlayer) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Zellnummer
            Text(
                text = cell.index.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSpecialCell) Color.White else Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            // Branch-Indikator
            if (isSpecialCell) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(
                            Color(0xFFFF6B35),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "⭐",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    @Composable
    fun EnhancedPlayerIndicator(
        playerIndex: Int,
        modifier: Modifier = Modifier
    ) {
        val color = getPlayerColor(playerIndex)
        Box(
            modifier = modifier
                .size(24.dp)
                .shadow(4.dp, CircleShape)
                .background(color, CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (playerIndex + 1).toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun PlayerNameTag(
        name: String,
        color: Color,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .background(color, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }

    /* --------------------------------------------------------------- Layout */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF0F4F8),
                        Color(0xFFE2E8F0)
                    )
                )
            )
            .padding(40.dp) // Mehr Padding für bessere Proportionen
    ) {

        /* ---------------- Top row --------------------------------------- */
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-30).dp)
                .padding(top = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 0 until sideCount) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Player name tag above cell
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        PlayerNameTag(name = name, color = color)
                    } ?: Spacer(Modifier.height(26.dp))

                    // Cell with overlaid players
                    Box {
                        EnhancedBoardCell(board[i], players)

                        // Player indicators
                        var playerOffset = 0
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) {
                                EnhancedPlayerIndicator(
                                    playerIndex = idx,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(
                                            x = (4 + playerOffset * 8).dp,
                                            y = (4 + playerOffset * 8).dp
                                        )
                                )
                                playerOffset++
                            }
                        }
                    }
                }
            }
        }

        /* ---------------- Right column ---------------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for (i in sideCount until 2 * sideCount) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cell with overlaid players
                    Box {
                        EnhancedBoardCell(board[i], players)

                        // Player indicators
                        var playerOffset = 0
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) {
                                EnhancedPlayerIndicator(
                                    playerIndex = idx,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(
                                            x = (4 + playerOffset * 8).dp,
                                            y = (4 + playerOffset * 8).dp
                                        )
                                )
                                playerOffset++
                            }
                        }
                    }

                    // Player name tag beside cell
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        PlayerNameTag(name = name, color = color)
                    } ?: Spacer(Modifier.width(80.dp))
                }
            }
        }

        /* ---------------- Bottom row (reverse) -------------------------- */
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 30.dp)
                .padding(bottom = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in (2 * sideCount until 3 * sideCount).reversed()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cell with overlaid players
                    Box {
                        EnhancedBoardCell(board[i], players)

                        // Player indicators
                        var playerOffset = 0
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) {
                                EnhancedPlayerIndicator(
                                    playerIndex = idx,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(
                                            x = (4 + playerOffset * 8).dp,
                                            y = (4 + playerOffset * 8).dp
                                        )
                                )
                                playerOffset++
                            }
                        }
                    }

                    // Player name tag below cell
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        PlayerNameTag(name = name, color = color)
                    } ?: Spacer(Modifier.height(26.dp))
                }
            }
        }

        /* ---------------- Left column (reverse) ------------------------- */
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for (i in (3 * sideCount until board.size).reversed()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Player name tag beside cell
                    getPlayerNameAtPosition(i)?.let { (name, color) ->
                        PlayerNameTag(name = name, color = color)
                    } ?: Spacer(Modifier.width(80.dp))

                    // Cell with overlaid players
                    Box {
                        EnhancedBoardCell(board[i], players)

                        // Player indicators
                        var playerOffset = 0
                        players.forEachIndexed { idx, p ->
                            if (p.position == i) {
                                EnhancedPlayerIndicator(
                                    playerIndex = idx,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(
                                            x = (4 + playerOffset * 8).dp,
                                            y = (4 + playerOffset * 8).dp
                                        )
                                )
                                playerOffset++
                            }
                        }
                    }
                }
            }
        }

        /* ---------------- Enhanced Centre label -------------------------- */
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎲 Mankomania",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF4299E1),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Lobby: $lobbyId",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/*------------------------------------------------------ Preview with dummy data */
@Preview(
    name = "Enhanced GameBoard – Landscape",
    showBackground = true,
    widthDp = 900,
    heightDp = 500
)
@Composable
private fun GameBoardContentPreview() {
    /* Dummy board: 20 cells, every 5th cell has a branch */
    val board = List(20) { idx -> CellDto(index = idx, hasBranch = idx % 5 == 0) }

    /* Dummy players */
    val players = listOf(
        PlayerDto(name = "Kevin", position = 2),
        PlayerDto(name = "Bob", position = 5),
        PlayerDto(name = "Clara", position = 11),
        PlayerDto(name = "Anna", position = 2) // Same position as Kevin for testing
    )

    MaterialTheme {
        GameBoardContent(
            board = board,
            players = players,
            lobbyId = "GAME123",
            playerNames = players.map { it.name }
        )
    }
}