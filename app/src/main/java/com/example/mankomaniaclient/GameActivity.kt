package com.example.mankomaniaclient.com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.screens.GameBoardScreen

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lobbyId = intent.getStringExtra("lobbyId") ?: "???"
        val playerNames = intent.getStringArrayListExtra("playerNames") ?: arrayListOf("P1", "P2")

        setContent {
            MaterialTheme {
                GameBoardScreen(playerNames = playerNames, lobbyId = lobbyId)
            }
        }
    }
}
