package com.example.mankomaniaclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.saveable.rememberSaveable

import com.example.mankomaniaclient.ui.screens.NameEntryScreen
import com.example.mankomaniaclient.network.WebSocketService


class NameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val playerName = rememberSaveable { mutableStateOf("") }
            val context = LocalContext.current

            NameEntryScreen(
                playerName = playerName.value,
                onNameChange = { playerName.value = it },
                onCreateLobby = {
                    WebSocketService.send("/app/register", playerName.value)
                    val intent = Intent(context, CreateLobbyActivity::class.java)
                    intent.putExtra("playerName", playerName.value)
                    context.startActivity(intent)
                },
                onJoinLobby = {
                    WebSocketService.send("/app/register", playerName.value)
                    val intent = Intent(context, JoinLobbyActivity::class.java)
                    intent.putExtra("playerName", playerName.value)
                    context.startActivity(intent)
                }
            )
        }
    }

}

