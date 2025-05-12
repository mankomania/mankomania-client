package com.example.mankomaniaclient.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoardScreen(playerName: String, lobbyId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎲 Game started!", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Player: $playerName")
        Text("Lobby ID: $lobbyId")
    }
}
