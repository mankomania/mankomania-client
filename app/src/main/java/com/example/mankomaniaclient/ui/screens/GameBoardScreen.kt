/*
 * @file GameBoardScreen.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description Lays the board cells around the edges (clockwise)
 *              and paints a neutral surface-variant as backdrop.
 */
package com.example.mankomaniaclient.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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


@Composable
fun GameBoardScreen(playerNames: List<String>,viewModel: GameViewModel) {
    val board by viewModel.board.collectAsState()
    Log.d("GameBoardScreen", "Board size=${board.size}")
    val players by viewModel.players.collectAsState()

    val sideCount = board.size / 4
    val bgColor = MaterialTheme.colorScheme.surfaceVariant   // << soft grey / blue by default

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
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
                BoardCellView(cell = board[i], players = players)
            }
        }

        /* ---------- Right column ------------------------------------------- */
        Column(Modifier.align(Alignment.CenterEnd)) {
            for (i in sideCount until 2 * sideCount) {
                Log.d("GameBoardScreen", "Right cell #$i → ${board.getOrNull(i)}")
                BoardCellView(cell = board[i], players = players)
            }
        }

        /* ---------- Bottom row (reverse) ----------------------------------- */
        Row(Modifier.align(Alignment.BottomCenter)) {
            for (i in (2 * sideCount until 3 * sideCount).reversed()) {
                Log.d("GameBoardScreen", "Bottom cell #$i → ${board.getOrNull(i)}")
                BoardCellView(cell = board[i], players = players)
            }
        }

        /* ---------- Left column (reverse) ---------------------------------- */
        Column(Modifier.align(Alignment.CenterStart)) {
            for (i in (3 * sideCount until board.size).reversed()) {
                Log.d("GameBoardScreen", "Left cell #$i → ${board.getOrNull(i)}")
                BoardCellView(cell = board[i], players = players)
            }
        }
    }
}


