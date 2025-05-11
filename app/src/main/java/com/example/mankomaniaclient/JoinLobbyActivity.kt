package com.example.mankomaniaclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.network.WebSocketService

class JoinLobbyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val webSocketService = WebSocketService

        setContent {
            val context = LocalContext.current
            val lobbyResponse by webSocketService.lobbyResponse.collectAsState(initial = null)
            val hasNavigated = remember { mutableStateOf(false) }

            LaunchedEffect(lobbyResponse) {
                if (!hasNavigated.value) {
                    lobbyResponse?.let { response ->
                        when (response.type) {
                            "joined" -> {
                                hasNavigated.value = true
                                val intent = Intent(context, CreateLobbyActivity::class.java).apply {
                                    putExtra("playerName", playerName)
                                    putExtra("lobbyId", response.lobbyId)
                                }
                                context.startActivity(intent)
                            }

                            "join-failed" -> {
                                hasNavigated.value = true
                                Toast.makeText(context, "Join failed – Lobby does not exist!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            JoinLobbyScreen(
                playerName = playerName,
                onJoinLobby = { lobbyId ->
                    webSocketService.joinLobby(lobbyId, playerName)
                }
            )
        }
    }
}

@Composable
fun JoinLobbyScreen(playerName: String, onJoinLobby: (String) -> Unit) {
    var lobbyId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello $playerName!", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = lobbyId,
            onValueChange = { lobbyId = it },
            placeholder = { Text("Enter Lobby ID") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onJoinLobby(lobbyId) },
            enabled = lobbyId.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Lobby")
        }
    }
}
