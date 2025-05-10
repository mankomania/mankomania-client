package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mankomaniaclient.ui.StartingMoneyScreen

class StartingMoneyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerId = intent.getStringExtra("playerId") ?: "default-player"

        setContent {
            StartingMoneyScreen(playerId = playerId)
        }
    }
}