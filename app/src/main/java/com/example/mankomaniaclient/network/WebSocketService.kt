package com.example.mankomaniaclient.network

import android.util.Log
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.body.TextFrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

import kotlinx.coroutines.*

class WebSocketService {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connect() {
        scope.launch {
            try {
                session = stompClient.connect("ws://10.0.2.2:8080/ws")
                Log.d("WebSocket", "Verbindung hergestellt")

                session?.subscribe(headers = StompSubscribeHeaders(destination = "/topic/greetings"))?.collect { message ->
                    Log.d("WebSocket", "Nachricht empfangen: ${message.bodyAsText}")
                }


            } catch (e: Exception) {
                Log.e("WebSocket", " Verbindung fehlgeschlagen: ${e.message}")
            }
        }
    }

    fun send(destination: String, message: String) {
        scope.launch {
            session?.send(
                headers = StompSendHeaders(destination = destination),
                body = TextFrameBody(message)
            )
        }
    }


}
