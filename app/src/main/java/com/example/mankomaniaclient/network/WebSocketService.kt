package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.viewmodel.GameViewModel
import android.util.Log
import com.example.mankomaniaclient.model.MoveResult
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
    private val jsonParser = Json { ignoreUnknownKeys = true }


    private var session: StompSession? = null
    private var connected = false

    private val _clientCount = MutableStateFlow(0)
    val clientCount: StateFlow<Int> = _clientCount.asStateFlow()
    private var gameViewModel: GameViewModel? = null

    fun clearLobbyResponse() {
        _lobbyResponse.value = null
    }

    fun setGameViewModel(vm: GameViewModel) {
        gameViewModel = vm
    }

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
                        val response = jsonParser.decodeFromString<LobbyResponse>(json)
                        Log.d("WebSocket", "Received lobby update: $response")

                        _lobbyResponse.value = response

                        if (response.players != null) {
                            _playersInLobby.value = response.players
                        }

                        if (response.type == "joined" || response.type == "created") {
                            Log.d(
                                "Lobby",
                                "Lobby ${response.lobbyId} has ${response.playerCount} players."
                            )
                        }
                    }
                }

                launch{
                    stomp.subscribeText("/topic/player-moved").collect { json ->
                        val moveResult = jsonParser.decodeFromString<MoveResult>(json)
                        Log.d("WebSocket", "Received move result: $moveResult")
                        gameViewModel?.onPlayerMoved(moveResult)
                            ?: Log.e("WebSocket", "ViewModel not yet set – skipping move update.")
                    }
                }

                // Subscribe to minigame events
                launch {
                    stomp.subscribeText("/topic/minigame/events").collect { json ->
                        Log.d("WebSocket", "Received minigame event: $json")
                        // Handle minigame events if needed
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
        val message = LobbyMessage(
            type = "join",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        send("/app/lobby", json)
    }

    fun createLobby(lobbyId: String, playerName: String) {
        val message = LobbyMessage(
            type = "create",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        send("/app/lobby", json)
    }

    fun startGame(lobbyId: String, playerName: String) {
        subscribeToLobby(lobbyId)
        val message = LobbyMessage(
            type = "start",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        Log.d("WS-START", "Sending start for lobby $lobbyId by $playerName")
        send("/app/lobby", jsonParser.encodeToString(LobbyMessage.serializer(), message))
    }

    /**
     * Initiates a minigame for the specified player in the given lobby.
     * Sends a request to the server to start the minigame.
     *
     * @param lobbyId The ID of the lobby where the minigame should start
     * @param playerId The ID of the player who will play the minigame
     */
    fun startMinigame(lobbyId: String, playerId: String) {
        val message = """{"lobbyId": "$lobbyId", "playerId": "$playerId"}"""
        send("/app/minigame/start", message)
        Log.d("WebSocket", "Minigame start request sent for player $playerId in lobby $lobbyId")
    }

    /**
     * Ends the currently active minigame and sends the result to the server.
     *
     * @param lobbyId The lobby ID where the minigame is active
     * @param playerId The player who completed the minigame
     * @param score The final score achieved in the minigame (if applicable)
     */
    fun endMinigame(lobbyId: String, playerId: String, score: Int = 0) {
        val message = """{"lobbyId": "$lobbyId", "playerId": "$playerId", "score": $score}"""
        send("/app/minigame/end", message)
        Log.d("WebSocket", "Minigame end request sent for player $playerId with score $score")
    }

    fun subscribeToLobby(lobbyId: String) {
        scope.launch {
            while (session == null) {
                delay(200)
            }

            launch {
                session!!
                    .subscribeText("/topic/lobby/$lobbyId")
                    .collect { json ->
                        val response = jsonParser.decodeFromString<LobbyResponse>(json)
                        Log.d("WebSocket", "Received lobby update (join): $response")

                        if (response.players != null) {
                            _playersInLobby.value = response.players
                        }

                        _lobbyResponse.value = response
                    }
            }

            launch {
                session!!
                    .subscribeText("/topic/game/state/$lobbyId")
                    .collect { json ->
                        Log.d("WS-STATE", "Got game state JSON: $json")
                        val state = jsonParser.decodeFromString<GameStateDto>(json)
                        Log.d("WebSocket", "Received game state: $state")
                        gameViewModel?.onGameState(state)
                    }
            }

            // Subscribe to minigame state updates for this specific lobby
            launch {
                session!!
                    .subscribeText("/topic/minigame/state/$lobbyId")
                    .collect { json ->
                        Log.d("WebSocket", "Received minigame state: $json")
                        // Update minigame state in the ViewModel when implemented
                        // gameViewModel?.onMinigameState(state)
                    }
            }
        }
    }
}