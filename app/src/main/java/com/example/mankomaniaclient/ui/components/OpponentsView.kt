/**
 * @file OpponentsView.kt
 * @author eles17
 * @since 2025-05-19
 * @description Displays all players' statuses in real time, excluding current user if desired.
 */

package com.example.mankomaniaclient.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.GameViewModel

/**
 * Renders a vertical list of player status cards for all tracked players.
 *
 * @param viewModel shared game state source (defaults to DI-injected instance)
 */
@Composable
fun OpponentsView(viewModel: GameViewModel = viewModel()) {
    val playerStatuses by viewModel.playerStatuses.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        playerStatuses.values.forEach { player ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (player.isTurn) "${player.name}" else player.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Position: ${player.position}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Balance: ${player.balance}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Money Breakdown:", style = MaterialTheme.typography.labelSmall)
                    player.money.forEach { (denomination, count) ->
                        Text(
                            text = "• $count x $denomination",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}