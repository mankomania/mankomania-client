package com.example.mankomaniaclient

import android.os.Bundle
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

    /**
     * Standard-Android-Lifecyclemethode.
     * Hier könnte zukünftig die UI gesetzt werden.
     */

    private val webSocketService = WebSocketService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = {
                        webSocketService.connect()
                    }) {
                        Text("Connect")
                    }
                    Button(onClick = {
                        webSocketService.send("/app/greetings", "hello local")
                    }) {
                        Text("Send Hello")
                    }
                    Text(
                        text = "→ schau in Logcat nach D/WebSocket …",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}