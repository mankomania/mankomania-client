package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.ui.screens.GameBoardScreen
import com.example.mankomaniaclient.ui.screens.WelcomeScreen
import java.util.UUID
import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import com.example.mankomaniaclient.ui.screens.LotteryScreen


class GameActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SCREEN = "extra_screen"
        const val SCREEN_WELCOME = "welcome"
        const val SCREEN_LOTTERY = "lottery"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenToShow = intent.getStringExtra(EXTRA_SCREEN) ?: SCREEN_WELCOME
        setContent {
            MaterialTheme {
                when (screenToShow) {
                    SCREEN_LOTTERY -> {
                        val tempPlayerId = UUID.randomUUID().toString()
                        val lotteryViewModel = LotteryViewModel()
                        lotteryViewModel.refreshAmount()

                        LotteryScreen(
                            playerId = tempPlayerId,
                            lotteryViewModel = lotteryViewModel
                        )
                    }

                    else -> {
                        WelcomeScreen(
                            onStartGame = {
                                setContent {
                                    MaterialTheme {
                                        GameBoardScreen()
                                    }
                                }
                            }
                        )
                    }
                    /*
                WelcomeScreen(
                    onStartGame = {
                        // Replace content with the actual board
                        setContent {
                            MaterialTheme { GameBoardScreen() }
                        }
                    }
                )*/
                }
            }
        }
    }
}
