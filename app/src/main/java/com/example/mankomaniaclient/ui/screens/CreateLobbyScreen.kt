package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateLobbyScreen(
    playerName: String,
    lobbyId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, $playerName!", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your Lobby ID: $lobbyId", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Waiting for other players to join...")
    }
}
