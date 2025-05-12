package com.example.mankomaniaclient.com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.screens.GameBoardScreen
import com.example.mankomaniaclient.ui.screens.WelcomeScreen

class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val lobbyId = intent.getStringExtra("lobbyId") ?: "???"
        setContent {
            MaterialTheme {
                WelcomeScreen(
                    onStartGame = {
                        // Replace content with the actual board
                        setContent {
                            MaterialTheme { GameBoardScreen(playerName = playerName, lobbyId = lobbyId) }
                        }
                    }
                )
            }
        }
    }

}

