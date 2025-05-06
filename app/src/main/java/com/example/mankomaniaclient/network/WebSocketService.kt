package com.example.mankomaniaclient.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText

/**
 * Service für WebSocket-Kommunikation via STOMP.
 *
 * @param stompClient injizierbarer STOMP-Client für bessere Testbarkeit
 * @param coroutineScope optionaler Coroutine-Scope (default: IO + SupervisorJob)
 */
class WebSocketService(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())) {

    private var session: StompSession? = null

    fun connect(url: String = "ws://se2-demo.aau.at:53210/ws", topic: String = "/topic/greetings") {
        coroutineScope.launch {
            try {
                session = stompClient.connect(url)
                Log.d("WebSocket", "Verbindung hergestellt")

                session
                    ?.subscribeText(destination = topic)
                    ?.collect { message ->
                        Log.d("WebSocket", "Nachricht empfangen: $message")
                    }

            } catch (e: Exception) {
                Log.e("WebSocket", "Verbindung fehlgeschlagen: ${e.message}")
            }
        }
    }

    fun send(destination: String, message: String) {
        coroutineScope.launch {
            session?.sendText(destination, message)
        }
    }
}