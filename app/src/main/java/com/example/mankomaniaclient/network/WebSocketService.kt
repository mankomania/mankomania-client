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

/**
 * Handles all STOMP communication with the game server.
 *  – keeps a single socket per app instance
 *  – exposes a StateFlow with the current lobby size
 *  – offers helper functions connect / send / disconnect
 */
class WebSocketService {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null

    /** extra guard so we never open a second socket */
    private var connected = false

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /* ---------- live lobby size ---------------------------------------- */
    private val _clientCount = MutableStateFlow(0)
    val clientCount: StateFlow<Int> = _clientCount.asStateFlow()

    /* ---------- connect ------------------------------------------------- */
    fun connect() {
        if (connected) return         // already online

        scope.launch {
            try {
                val stomp = stompClient.connect("ws://se2-demo.aau.at:53210/ws")
                session = stomp
                connected = true
                Log.d("WebSocket", "Connection established")

                /* greetings --------------------------------------------- */
                launch {
                    stomp.subscribeText("/topic/greetings").collect { msg ->
                        Log.d("WebSocket", "Greeting received: $msg")
                    }
                }

                /* client counter ---------------------------------------- */
                launch {
                    stomp.subscribeText("/topic/clientCount").collect { raw ->
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

    /* ---------- send ---------------------------------------------------- */
    fun send(destination: String, message: String) {
        scope.launch {
            try {
                session?.sendText(destination, message)
                withContext(Dispatchers.Main) {
                    Log.d("WebSocket", "Message sent: $message")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("WebSocket", "Send error: ${e.message}")
                }
            }
        }
    }

    /* ---------- disconnect --------------------------------------------- */
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