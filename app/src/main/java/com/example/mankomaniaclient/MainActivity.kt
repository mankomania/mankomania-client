/**
 * # MainActivity
 *
 * Main entry point of the Android app. It initializes the user interface
 * and establishes a WebSocket connection immediately upon creation.
 * This activity manages the UI for showing live client metrics and handling
 * button actions for WebSocket interactions (connect, send message).
 *
 * Additionally, it provides a button to transition to the `WelcomeScreen.kt`
 * which serves as the entry point to the game (`Mankomania`). Upon clicking
 * the "Play Mankomania" button, the user is taken to the `WelcomeScreen.kt`
 * to begin the game session.
 *
 * Lifecycle:
 * `onStart()` → `connect()`   |  • `onStop()` → `disconnect()`
 *
 * @author Angela Drucks
 * @since sprint-1
 * @description This activity is responsible for connecting to the WebSocket server
 *              when the app starts, displaying the current number of connected clients,
 *              providing buttons to trigger WebSocket actions like sending messages,
 *              and managing the transition to the `WelcomeScreen.kt` where the game starts.
 */

package com.example.mankomaniaclient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable

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
import com.example.mankomaniaclient.network.WebSocketService

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    val webSocketService = WebSocketService


    /* --------------------------------------------------------------------- */
    /* Lifecycle                                                             */
    /* --------------------------------------------------------------------- */

    /**
     * Called when the activity is starting. This is where the Compose UI is set up.
     * It applies the global theme and shows the main game board screen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        webSocketService.connect()

        setContent {
            val clientCount by webSocketService.clientCount.collectAsState()
            val context = LocalContext.current

            MainScreen(
                clientCount = clientCount,
                onConnect = {
                    Toast.makeText(context, "Connect geklickt", Toast.LENGTH_SHORT).show()
                    webSocketService.connect()
                },
                onSendHello = {
                    Toast.makeText(context, "Send Hello geklickt", Toast.LENGTH_SHORT).show()
                    webSocketService.send("/app/greetings", "hello local")
                },
                onPlay = {
                    startActivity(Intent(this, LoadingActivity::class.java))
                }
            )
        }
    }
    fun onGameExit() {
        WebSocketService.disconnect()
        finish()
    }

}

/* ------------------------------------------------------------------------- */
/* UI‑Composable                                                             */
/* ------------------------------------------------------------------------- */

@Composable
private fun MainScreen(
    clientCount: Int,
    onConnect: () -> Unit,
    onSendHello: () -> Unit,
    onPlay: () -> Unit
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onConnect) { Text("Connect") }
                Button(onClick = onSendHello) { Text("Send Hello") }
            }

            Text(
                text = "Aktive Clients: $clientCount",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "→ schau in Logcat nach D/WebSocket …",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onPlay) { Text("Play Mankomania") }
            // Apply the custom Material3 theme for the whole app
            GlobalTheme {
                // Launch the main game screen
                GameBoardScreen()
            }
        }
    }
}