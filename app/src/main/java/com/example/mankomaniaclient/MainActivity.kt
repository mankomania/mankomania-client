package com.example.mankomaniaclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.network.WebSocketService

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val webSocketService = WebSocketService

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
                },
                onOpenLottery = {
                    startActivity(Intent(this, LotteryActivity::class.java))
                },
                onViewStartingMoney = {
                    val intent = Intent(context, StartingMoneyActivity::class.java)
                    intent.putExtra("playerId", "test-player-123")
                    context.startActivity(intent)
                }
            )
        }
    }

    fun onGameExit() {
        WebSocketService.disconnect()
        finish()
    }
}

@Composable
private fun MainScreen(
    clientCount: Int,
    onConnect: () -> Unit,
    onSendHello: () -> Unit,
    onPlay: () -> Unit,
    onOpenLottery: () -> Unit,
    onViewStartingMoney: () -> Unit
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
            Button(onClick = onViewStartingMoney) { Text("View Starting Money") }
        }
    }
}