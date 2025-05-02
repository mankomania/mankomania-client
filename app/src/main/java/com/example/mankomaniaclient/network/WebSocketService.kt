package com.example.mankomaniaclient.network

import android.util.Log
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebSocketService {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // ---------- NEW: StateFlow for active‑client metric ----------
    private val _clientCount = MutableStateFlow(0)
    val clientCount: StateFlow<Int> get() = _clientCount.asStateFlow()


    fun connect() {
        scope.launch {
            try {
                session = stompClient.connect("ws://se2-demo.aau.at:53210/ws")

                withContext(Dispatchers.Main) {
                    Log.d("WebSocket", "Verbindung hergestellt")
                }

                // A) /topic/greetings
                session?.subscribeText("/topic/greetings")?.collect { msg ->
                    withContext(Dispatchers.Main) {
                        Log.d("WebSocket", "Nachricht empfangen: $msg")
                    }
                }

                // --------- NEW: subscribe to /topic/clientCount ----------
                launch {
                    session?.subscribeText("/topic/clientCount")?.collect { countStr ->
                        val value = countStr.toIntOrNull() ?: 0
                        _clientCount.value = value
                        withContext(Dispatchers.Main) {
                            Log.d("WebSocket", "Aktive Clients: $value")
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("WebSocket", "Verbindung fehlgeschlagen: ${e.message}")
                }
            }
        }
    }

    fun send(destination: String, message: String) {
        scope.launch {
            try {
                session?.sendText(destination, message)
                withContext(Dispatchers.Main) {
                    Log.d("WebSocket", "Nachricht gesendet: $message")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("WebSocket", "Fehler beim Senden: ${e.message}")
                }
            }
        }
    }

    // Close function for client counter
    fun disconnect() {
        scope.launch {
            session?.disconnect()
            session = null
            _clientCount.value = 0
        }
    }
}
