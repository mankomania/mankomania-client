package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun JoinLobbyScreen(
    playerName: String,
    onJoinLobby: (String) -> Unit
) {
    var lobbyId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Hello $playerName!", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = lobbyId,
            onValueChange = { lobbyId = it },
            label = { Text("Lobby ID") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        StyledButton(text = "Join Lobby", onClick = {
            // TODO: Send WebSocket or REST request to join lobby
        })
    }
}
