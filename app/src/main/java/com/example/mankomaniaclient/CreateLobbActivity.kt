package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

class CreateLobbyActivity : ComponentActivity() {
    val lobbyId = generateLobbyId()
    private fun generateLobbyId(length: Int = 6): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val lobbyId = generateLobbyId()

        setContent {
            CreateLobbyScreen(
                playerName = playerName,
                lobbyId = lobbyId
            )
        }
    }
}
@Composable
fun CreateLobbyScreen(playerName: String, lobbyId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello $playerName!")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Lobby ID: $lobbyId", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Waiting for other players...")

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* TODO: Start game */ },
            enabled = false // später aktivieren wenn >=2 Spieler
        ) {
            Text("Start Game")
        }
    }
}
