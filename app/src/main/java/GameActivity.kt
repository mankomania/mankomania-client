package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.mankomaniaclient.ui.screens.GameBoardScreen

class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameBoardScreen() // TO DO: Logik
            }
        }
    }
}