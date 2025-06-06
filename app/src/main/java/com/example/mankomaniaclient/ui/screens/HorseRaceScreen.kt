package com.example.mankomaniaclient.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.ui.components.HorseColor
import com.example.mankomaniaclient.ui.components.HorseRaceVisual
import com.example.mankomaniaclient.ui.viewmodel.HorseRaceViewModel

@Composable
fun HorseRaceScreen(viewModel: HorseRaceViewModel) {
    val winner by viewModel.winner.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Horse Race",
            style = MaterialTheme.typography.headlineMedium
        )

        // Visual representation of the horses
        HorseRaceVisual(winner = winner)

        Button(onClick = { viewModel.spinRoulette() }) {
            Text("Spin Roulette")
        }

        if (winner != null) {
            Text(
                text = "The winning horse is: ${winner!!.displayName}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}