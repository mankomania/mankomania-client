package com.example.mankomaniaclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mankomaniaclient.ui.StartingMoneyScreen

class StartingMoneyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract the player ID from the intent
        val playerId = intent.getStringExtra(PLAYER_ID_KEY) ?: ""
        Log.d("StartingMoneyActivity", "Player ID: $playerId")

        setContent {
            StartingMoneyScreen(playerId = playerId)
        }
    }

    companion object {
        const val PLAYER_ID_KEY = "player_id"

        // Helper method to create an Intent for StartingMoneyActivity
        fun createIntent(playerId: String): Intent {
            return Intent().apply {
                putExtra(PLAYER_ID_KEY, playerId)
            }
        }
    }
}