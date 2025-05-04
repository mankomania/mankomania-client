package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mankomaniaclient.ui.screens.GameBoardScreen
import com.example.mankomaniaclient.ui.theme.GlobalTheme

/**
 * @file MainActivity.kt
 * @author eles17
 * @since 04.05.2025
 * @description
 * Main entry point of the Mankomania Android client.
 * This activity sets the initial UI content using Jetpack Compose.
 * It wraps the entire game UI in a global theme and launches the GameBoardScreen.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. This is where the Compose UI is set up.
     * It applies the global theme and shows the main game board screen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the custom Material3 theme for the whole app
            GlobalTheme {
                // Launch the main game screen
                GameBoardScreen()
            }
        }
    }
}