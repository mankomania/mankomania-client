package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * # WelcomeScreen
 *
 * First screen shown after tapping **“Play Mankomania”**.
 *
 * ## TODO for the next UI iteration
 * 1. **Create player**
 *    – Text field for the player name.
 *    – "Create player" button saves the name into `GameViewModel`.
 * 2. **Lobby overview**
 *    – Show a list of currently connected players (later via WebSocket).
 *    – Enable the "Start game" button once ≥ 2 players are present.
 * 3. **Navigation**
 *    – When "Start game" is pressed, navigate to `GameBoardScreen`.
 * 4. **Branding**
 *    – Use colors & typography from `GlobalTheme.kt`.
 * 5. **Accessibility**
 *    – Add content‑descriptions and proper semantics.
 *
 * This file is only a stub – feel free to redesign the layout.
 * The only requirement: the composable must be named **WelcomeScreen**.
 */
@Composable
fun WelcomeScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mankomania", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Player name") },
            singleLine = true
        )

        Button(
            onClick = { /* TODO create player */ },
            enabled = playerName.isNotBlank()
        ) { Text("Create player") }

        HorizontalDivider(thickness = 1.dp)

        Button(
            onClick = onStartGame,
            enabled = false // TODO enable once conditions are met
        ) { Text("Start game") }
    }
}
