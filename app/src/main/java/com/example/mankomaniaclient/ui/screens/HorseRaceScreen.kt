package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.ui.components.HorseColor
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel

@Composable
fun HorseRaceScreen(viewModel: HorseRaceViewModel) {
    val winner by viewModel.winner.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Horse Race",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("Title")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lista cavalli colorati con testTag univoci
        HorseColor.values().forEach { horse ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when (horse) {
                            HorseColor.RED -> MaterialTheme.colorScheme.errorContainer
                            HorseColor.BLUE -> MaterialTheme.colorScheme.primaryContainer
                            HorseColor.YELLOW -> MaterialTheme.colorScheme.secondaryContainer
                            HorseColor.GREEN -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                    .padding(12.dp)
                    .testTag("HorseColor_${horse.name}") // TAG UNIVOCO per ogni colore
            ) {
                Text(horse.displayName)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.spinRoulette() },
            modifier = Modifier.testTag("SpinButton")
        ) {
            Text("Spin Roulette")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (winner != null) {
            Text(
                "The winning horse is: ${winner!!.displayName}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("WinnerText")
            )
        }

        // Qui puoi aggiungere radio buttons o altri elementi con testTag per i test
    }
}