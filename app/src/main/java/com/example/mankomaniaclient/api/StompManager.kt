package com.example.mankomaniaclient.api
/**
 * @file StompManager.kt
 * @author eles17
 * @since 03.05.2025
 * @description
 * Singleton object responsible for managing the WebSocket STOMP connection.
 * It connects to the backend server, sends dice roll requests, and listens for dice result messages.
 */
import com.example.mankomaniaclient.config.NetworkConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

/**
 * Manages WebSocket STOMP communication for dice rolls.
 * Connects, subscribes to result updates, and sends dice roll requests.
 */
object StompManager {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val _diceResultFlow = MutableSharedFlow<String>()
    val diceResultFlow = _diceResultFlow.asSharedFlow()

    /**
     * Connects and subscribes to dice result topic for given player.
     */
    fun connectAndSubscribe(playerId: String) {
        coroutineScope.launch {
            mutex.withLock {
                if (session == null) {
                    println("Connecting to WebSocket at ws://10.0.2.2:8080/ws")
                    session = stompClient.connect(NetworkConfig.WS_URL)
                    println("Connected to WebSocket!")
                }
            }

            val headers = StompSubscribeHeaders(destination = "/topic/diceResult/$playerId")
            session?.subscribe(headers)?.let { flow ->
                launch {
                    flow.collect { frame ->
                        val body = (frame.body as? FrameBody.Text)?.text ?: ""
                        println("Received dice result: $body")
                        _diceResultFlow.emit(body)
                    }
                }
            }
        }
    }

    /**
     * Sends a dice roll request to backend for specified player.
     */
    fun sendRollRequest(playerId: String) {
        coroutineScope.launch {
            mutex.withLock {
                if (session == null) {
                    println("Connecting to WebSocket at ws://10.0.2.2:8080/ws")
                    session = stompClient.connect(NetworkConfig.WS_URL)
                }
            }

            println("Sending rollDice request for player: $playerId")
            val headers = StompSendHeaders(destination = "/app/rollDice")
            session?.send(headers, FrameBody.Text(playerId))
        }
    }
}