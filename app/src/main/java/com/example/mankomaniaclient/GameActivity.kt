package com.example.mankomaniaclient.com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.ui.screens.GameBoardScreen
import androidx.compose.runtime.remember
import com.example.mankomaniaclient.viewmodel.GameViewModel


class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val lobbyId = intent.getStringExtra("lobbyId") ?: "???"
        setContent {
            val viewModel = remember { GameViewModel() }
            MaterialTheme {
                GameBoardScreen(viewModel = viewModel)
            }
        }

    }

}

