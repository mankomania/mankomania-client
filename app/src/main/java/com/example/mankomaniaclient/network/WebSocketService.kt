package com.example.mankomaniaclient.network

import android.util.Log
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles all STOMP communication with the game server.
 *  – keeps a single socket per app instance
 *  – exposes a StateFlow with the current lobby size
 *  – offers helper functions connect / send / disconnect
 */
class WebSocketService(
    private val stompClient: StompClient = StompClient(OkHttpWebSocketClient()),
    private val scope: CoroutineScope   = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main          // <-- NEW
) {

    private var session: StompSession? = null
    private var connected = false

    private val _clientCount = MutableStateFlow(0)
    val clientCount: StateFlow<Int> = _clientCount.asStateFlow()

    fun connect(
        url: String            = "ws://se2-demo.aau.at:53210/ws",
        greetingsTopic: String = "/topic/greetings",
        clientCountTopic: String = "/topic/clientCount"
    ) {
        if (connected) return

        scope.launch {
            try {
                val stomp = stompClient.connect(url)
                session = stomp
                connected = true
                Log.d("WebSocket", "Connection established")

                launch {
                    stomp.subscribeText(greetingsTopic).collect {
                        Log.d("WebSocket", "Greeting received: $it")
                    }
                }

                launch {
                    stomp.subscribeText(clientCountTopic).collect { raw ->
                        val value = raw.toIntOrNull() ?: 0
                        _clientCount.value = value
                        Log.d("WebSocket", "Active clients: $value")
                    }
                }
            } catch (e: Exception) {
                connected = false
                Log.e("WebSocket", "Connection failed: ${e.message}")
            }
        }
    }

    fun send(destination: String, message: String) {
        scope.launch {
            try {
                session?.sendText(destination, message)
                withContext(uiDispatcher) {
                    Log.d("WebSocket", "Message sent: $message")
                }
            } catch (e: Exception) {
                withContext(uiDispatcher) {
                    Log.e("WebSocket", "Send error: ${e.message}")
                }
            }
        }
    }

    fun disconnect() {
        scope.launch {
            session?.disconnect()
            session = null
            connected = false
            _clientCount.value = 0
            Log.d("WebSocket", "Disconnected")
        }
    }
}