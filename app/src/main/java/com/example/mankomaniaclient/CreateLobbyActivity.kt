package com.example.mankomaniaclient

import com.example.mankomaniaclient.network.WebSocketService
import com.example.mankomaniaclient.GameActivity

import android.util.Log
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class CreateLobbyActivity : ComponentActivity() {
    private fun generateLobbyId(length: Int = 6): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val intentLobbyId = intent.getStringExtra("lobbyId")

        WebSocketService.connect()

        setContent {
            CreateLobbyContent(playerName = playerName, lobbyIdFromIntent = intentLobbyId)
        }
    }

    @Composable
    fun CreateLobbyContent(playerName: String, lobbyIdFromIntent: String?) {
        val context = LocalContext.current
        val lobbyResponse by WebSocketService.lobbyResponse.collectAsState()
        val finalLobbyId = remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            val id: String = if (lobbyIdFromIntent == null) {
                val newId = generateLobbyId()
                WebSocketService.createLobby(newId, playerName)
                newId
            } else {
                WebSocketService.subscribeToLobby(lobbyIdFromIntent!!)
                lobbyIdFromIntent
            }

            finalLobbyId.value = id
        }

        LaunchedEffect(lobbyResponse) {
            if (lobbyResponse?.type == "start") {
                val players = WebSocketService.playersInLobby.value
                val intent = Intent(context, GameActivity::class.java).apply {
                    putStringArrayListExtra("playerNames", ArrayList(players))
                    putExtra("playerName", playerName)
                    putExtra("lobbyId", finalLobbyId.value)
                    putExtra(GameActivity.EXTRA_SCREEN, GameActivity.SCREEN_GAMEBOARD)

                }
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            }
        }
        CreateLobbyScreen(playerName = playerName, lobbyId = finalLobbyId.value)
    }

    @Composable
    fun CreateLobbyScreen(playerName: String, lobbyId: String) {
        val players by WebSocketService.playersInLobby.collectAsState()

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

            Spacer(modifier = Modifier.height(24.dp))

            if (players.isNotEmpty()) {
                Text("Players:")
                players.forEach {
                    Text("- $it")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    WebSocketService.subscribeToLobby(lobbyId)
                    Log.d("LOBBY", "Trying to start game in $lobbyId by $playerName")
                    WebSocketService.startGame(lobbyId, playerName)
                },
                enabled = players.size >= 2
            ) {
                Text("Start Game")
            }
        }
    }
}
