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


import com.example.mankomaniaclient.network.WebSocketService

/**
 * # MainActivity
 *
 * Haupt-Einstiegspunkt der Android-App.
 *
 * @author Angela Drucks
 * @since sprint-1
 * @description Diese Activity wird beim App-Start geladen und initialisiert die UI.
 */
class MainActivity : ComponentActivity() {

    private val webSocketService = WebSocketService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("WebSocket", "MainActivity onCreate aufgerufen")

        // immediately establish WS connection, so the counter appears right away
        webSocketService.connect()

        setContent {
            val context = LocalContext.current
            // collect the current client count as Compose state
            val clientCount by webSocketService.clientCount.collectAsState()

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            Toast.makeText(context, "Connect geklickt", Toast.LENGTH_SHORT).show()
                            webSocketService.connect()
                        }) { Text("Connect") }
                        Button(onClick = {
                            Toast.makeText(context, "Send Hello geklickt", Toast.LENGTH_SHORT).show()
                            webSocketService.send("/app/greetings", "hello local")
                        }) { Text("Send Hello") }
                    }

                    // live metric
                    Text(
                        text = "Aktive Clients: $clientCount",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "→ schau in Logcat nach D/WebSocket …",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = {
                        val intent = Intent(context, GameActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Play Mankomania")
                    }

                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}