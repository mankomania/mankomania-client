package com.example.mankomaniaclient.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.GameViewModel

/**
 * @file StatusBarView.kt
 * @author eles17
 * @since 2025-05-14
 * @description Displays the current player's live status, including name, position, and balance.
 */

/**
 * Displays the status of the current player in a card view.
 *
 * @param viewModel the shared GameViewModel containing all player statuses
 * @param playerName the name of the current player to track
 */
@Composable
fun StatusBarView(
    viewModel: GameViewModel = viewModel(),
    playerName: String
) {
    val playerStatuses by viewModel.playerStatuses.collectAsState()
    val current = playerStatuses[playerName]

    current?.let { player ->
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("You: ${player.name}", style = MaterialTheme.typography.titleMedium)
                Text("Position: ${player.position}", style = MaterialTheme.typography.bodyMedium)
                Text("Balance: ${player.balance}", style = MaterialTheme.typography.bodyMedium)
                if (player.isActive) {
                    Text("Your turn!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}