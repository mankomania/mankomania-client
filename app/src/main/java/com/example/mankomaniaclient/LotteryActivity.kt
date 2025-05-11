package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mankomaniaclient.ui.screens.LotteryScreen
import com.example.mankomaniaclient.ui.theme.MankomaniaclientTheme
import com.example.mankomaniaclient.viewmodel.LotteryViewModel
import java.util.UUID

class LotteryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MankomaniaclientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // temporary player-id for testing
                    val tempPlayerId = UUID.randomUUID().toString()
                    val lotteryViewModel = LotteryViewModel()

                    lotteryViewModel.refreshAmount()

                    LotteryScreen(
                        playerId = tempPlayerId,
                        lotteryViewModel = lotteryViewModel
                    )
                }
            }
        }
    }
}