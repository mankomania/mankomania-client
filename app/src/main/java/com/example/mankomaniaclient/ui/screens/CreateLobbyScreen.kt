package com.example.mankomaniaclient.ui.screens

import com.example.mankomaniaclient.network.WebSocketService
import com.example.mankomaniaclient.GameActivity

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext


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
        val players by WebSocketService.playersInLobby.collectAsState()
        Text(
            text = "Hello ${players.joinToString(", ")}!",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your Lobby ID: $lobbyId", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Waiting for other players...")

        val context = LocalContext.current

        if (players.size >= 2) {
            Button(onClick = {
                WebSocketService.startGame(lobbyId, playerName)
            }) {
                Text("Start Game")
            }
        }


    }
}
