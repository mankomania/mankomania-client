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
import kotlinx.serialization.json.Json
import org.json.JSONObject
import kotlinx.serialization.encodeToString


/**
 * Handles all STOMP communication with the game server.
 *  – keeps a single socket per app instance
 *  – exposes a StateFlow with the current lobby size
 *  – offers helper functions connect / send / disconnect
 */
object WebSocketService {
    private val stompClient: StompClient = StompClient(OkHttpWebSocketClient())
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
    private val _lobbySize = MutableStateFlow(1)
    private val _playersInLobby = MutableStateFlow<List<String>>(emptyList())
    val playersInLobby: StateFlow<List<String>> = _playersInLobby.asStateFlow()
    private val _lobbyResponse = MutableStateFlow<LobbyResponse?>(null)
    val lobbyResponse: StateFlow<LobbyResponse?> = _lobbyResponse.asStateFlow()


    private var session: StompSession? = null
    private var connected = false

    private val _clientCount = MutableStateFlow(0)
    val clientCount: StateFlow<Int> = _clientCount.asStateFlow()


    fun connect(
        url: String = "ws://se2-demo.aau.at:53210/ws",
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
                    stomp.subscribeText("/topic/register").collect {
                        Log.d("WebSocket", "Register broadcast: $it")
                    }
                }

                launch {
                    stomp.subscribeText(clientCountTopic).collect { raw ->
                        val value = raw.toIntOrNull() ?: 0
                        _clientCount.value = value
                        Log.d("WebSocket", "Active clients: $value")
                    }
                }

                launch {
                    stomp.subscribeText("/topic/lobby").collect { json ->
                        val response = Json.decodeFromString<LobbyResponse>(json)
                        Log.d("WebSocket", "Received lobby update: $response")

                        _lobbyResponse.value = response

                        if (response.type == "joined" || response.type == "created") {
                            Log.d(
                                "Lobby",
                                "Lobby ${response.lobbyId} has ${response.playerCount} players."
                            )
                        }
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


    fun joinLobby(lobbyId: String, playerName: String) {
        Log.d("WebSocket", "Joining lobby $lobbyId as $playerName")
        val message = JSONObject()
        message.put("type", "join")
        message.put("lobbyId", lobbyId)
        message.put("playerName", playerName)
        send(destination = "/topic/lobby", message = message.toString())
    }

    fun createLobby(lobbyId: String, playerName: String) {
        val message = LobbyMessage(
            type = "create",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = Json.encodeToString(message)
        send("/app/lobby", json)
    }

    fun startGame(lobbyId: String, playerName: String) {
        val message = LobbyMessage(
            type = "start",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = Json.encodeToString(message)
        send("/app/lobby", json)
    }

}