package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mankomaniaclient.api.StompManager
import com.example.mankomaniaclient.model.DiceResult
import com.example.mankomaniaclient.ui.components.DiceView
import com.example.mankomaniaclient.viewmodel.GameViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * @file GameBoardScreen.kt
 * @author eles17
 * @since 04.05.2025
 * @description
 * Main game screen that allows players to trigger dice rolls and view results.
 * Subscribes to WebSocket via StompManager and updates GameViewModel accordingly.
 */

@Composable
fun GameBoardScreen(gameViewModel: GameViewModel = viewModel()) {
    val diceResult by gameViewModel.diceResult.collectAsState()
    val playerId = "blue" // Later should be dynamic when multiplayer is added

    // Establish WebSocket connection and collect dice results
    LaunchedEffect(Unit) {
        StompManager.connectAndSubscribe(playerId)
        StompManager.diceResultFlow.collectLatest { resultJson ->
            try {
                val numbers = Regex("""\d+""").findAll(resultJson).map { it.value.toInt() }.toList()
                if (numbers.size >= 2) {
                    gameViewModel.receiveDiceResult(DiceResult(numbers[0], numbers[1]))
                } else {
                    println("Invalid dice data: $resultJson")
                }
            } catch (e: Exception) {
                println("Failed to parse dice result: $e")
            }
        }
    }

    // UI for rolling dice and showing the result
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Willkommen im Mankomania-Spiel!", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = { gameViewModel.rollDice(playerId) },
            enabled = true // TODO: restrict to player's turn
        ) {
            Text("Roll Dice")
        }

        diceResult?.let {
            DiceView(result = it)
        }
    }
}