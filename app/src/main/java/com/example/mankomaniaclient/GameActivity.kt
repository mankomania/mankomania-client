package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.ui.screens.GameBoardScreen
import com.example.mankomaniaclient.ui.screens.WelcomeScreen
import com.example.mankomaniaclient.viewmodel.GameViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @file GameActivity.kt
 * @author Angela Drucks, Lev Starman, Anna-Paloma Walder
 * @since 2025-05-13
 * @description
 *   Main entry point for the game UI.
 *   Shows a welcome screen first, then loads the game board when the user starts.
 */
class GameActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SCREEN    = "extra_screen"
        const val SCREEN_WELCOME  = "welcome"
        const val SCREEN_LOTTERY  = "lottery"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve player name and lobby ID from the Intent (fallbacks)
        val playerName = intent.getStringExtra("playerName") ?: "Unknown"
        val lobbyId    = intent.getStringExtra("lobbyId")    ?: "???"

        // Create your ViewModel here (lifecycle‐aware, no extra Compose dependency)
        val gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        setContent {
            MaterialTheme {
                WelcomeScreen(
                    onStartGame = {
                        // swap in the actual game board
                        setContent {
                            MaterialTheme {
                                GameBoardScreen(viewModel = gameViewModel)
                            }
                        }
                    }
                )
            }
        }
    }
}
