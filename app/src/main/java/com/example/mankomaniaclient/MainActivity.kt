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


import com.example.mankomaniaclient.network.WebSocketService

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val webSocketService = WebSocketService()

    /* --------------------------------------------------------------------- */
    /* Lifecycle                                                             */
    /* --------------------------------------------------------------------- */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

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
                    //startActivity(Intent(this, GameActivity::class.java))
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra(GameActivity.EXTRA_SCREEN, GameActivity.SCREEN_WELCOME)
                    startActivity(intent)
                },
                onOpenLottery = {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra(GameActivity.EXTRA_SCREEN, GameActivity.SCREEN_LOTTERY)
                    startActivity(intent)
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() → connect()")
        webSocketService.connect()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() → disconnect()")
        webSocketService.disconnect()
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
    onPlay: () -> Unit,
    onOpenLottery: () -> Unit
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

            Button(onClick = onOpenLottery) { Text("Open Lottery") }

        }
    }
}