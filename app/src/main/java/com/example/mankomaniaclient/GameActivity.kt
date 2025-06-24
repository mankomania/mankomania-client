package com.example.mankomaniaclient
import com.example.mankomaniaclient.network.WebSocketService
import android.content.Intent
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
 *   Routes between Welcome, Lottery (future), and the GameBoard.
 */
class GameActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SCREEN     = "extra_screen"
        const val SCREEN_WELCOME   = "welcome"
        const val SCREEN_LOTTERY   = "lottery"
        const val SCREEN_GAMEBOARD = "gameboard"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve both single-name fallback and full list from Intent
        val singleName   = intent.getStringExtra("playerName") ?: "Unknown"
        val playerNames  = intent.getStringArrayListExtra("playerNames")
            ?: arrayListOf(singleName)

        val lobbyId = intent.getStringExtra("lobbyId")
            ?: throw IllegalStateException("Missing lobbyId in Intent")

        // Create ViewModel (lifecycle-aware, no extra Compose dependency)
        val gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        gameViewModel.setMyPlayerName(singleName)

        WebSocketService.setGameViewModel(gameViewModel)
        WebSocketService.subscribeToLobby(lobbyId)

        // Decide which screen to show based on the EXTRA_SCREEN flag
        when (intent.getStringExtra(EXTRA_SCREEN)) {

            SCREEN_GAMEBOARD -> {
                // Directly show the main GameBoard, using the full list!
                setContent {
                    MaterialTheme {
                        GameBoardScreen(
                            lobbyId = lobbyId,
                            playerNames = playerNames,
                            viewModel   = gameViewModel,
                            myPlayerName = singleName
                        )
                    }
                }
            }

            SCREEN_LOTTERY -> {
                // Placeholder for future Lottery mini-game
                setContent {
                    MaterialTheme {
                        // LotteryScreen(/* … */)
                    }
                }
            }

            else -> {
                // Default: show WelcomeScreen, then navigate to GameBoard on start
                setContent {
                    MaterialTheme {
                        WelcomeScreen(
                            onStartGame = {
                                // Restart this Activity with GAMEBOARD flag and full list
                                val next = Intent(this, GameActivity::class.java).apply {
                                    putStringArrayListExtra(
                                        "playerNames",
                                        ArrayList(playerNames)
                                    )
                                    putExtra("playerName", singleName)
                                    putExtra("lobbyId", lobbyId)
                                    putExtra(EXTRA_SCREEN, SCREEN_GAMEBOARD)
                                }
                                startActivity(next)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}