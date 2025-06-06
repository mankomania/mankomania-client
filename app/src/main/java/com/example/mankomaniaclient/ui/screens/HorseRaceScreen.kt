package com.example.mankomaniaclient.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.ui.components.HorseColor
import com.example.mankomaniaclient.ui.components.HorseRaceVisual
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel

@Composable
fun HorseRaceScreen(viewModel: HorseRaceViewModel) {
    val winner by viewModel.winner.collectAsState()
    val selectedHorse by viewModel.selectedHorse.collectAsState()
    val betResult by viewModel.betResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text("Horse Race", style = MaterialTheme.typography.headlineMedium)

        HorseRaceVisual(winner = winner)

        Button(onClick = { viewModel.spinRoulette() }) {
            Text("Spin Roulette")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Place your bet:", style = MaterialTheme.typography.titleMedium)

        // Radio buttons for horse selection
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorseColor.values().forEach { horse ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = horse == selectedHorse,
                        onClick = { viewModel.selectedHorse.value = horse }
                    )
                    Text(horse.displayName)
                }
            }
        }

        Button(onClick = { viewModel.placeBet() }) {
            Text("Place Bet")
        }

        betResult?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, style = MaterialTheme.typography.titleMedium)
        }

        if (winner != null) {
            Text(
                text = "The winning horse is: ${winner!!.displayName}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}