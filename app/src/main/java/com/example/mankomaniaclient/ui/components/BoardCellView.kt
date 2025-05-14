
/*
 * @file BoardCellView.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description Composable that renders a single board cell, coloring branch cells and
 *              showing a player's initial if that cell is occupied.
 *
 * @param cell    The cell's index and branch flag.
 * @param players The current list of players and their positions.
 */

package com.example.mankomaniaclient.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.network.CellDto
import com.example.mankomaniaclient.network.PlayerDto


@Composable
fun BoardCellView(cell: CellDto, players: List<PlayerDto>) {
    val occupant = players.firstOrNull { it.position == cell.index }
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .background(
                if (cell.hasBranch)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = occupant?.name?.first()?.toString() ?: cell.index.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
