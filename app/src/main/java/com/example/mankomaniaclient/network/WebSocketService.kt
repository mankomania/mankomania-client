package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.viewmodel.GameViewModel
import android.util.Log
import com.example.mankomaniaclient.model.DiceResult
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

    /**
     * Clears the current lobby response
     */
    fun clearLobbyResponse() {
        _lobbyResponse.value = null
    }

    /**
     * Sets the GameViewModel for callbacks
     */
    fun setGameViewModel(vm: GameViewModel) {
        gameViewModel = vm
    }

    /**
     * Establishes a connection to the WebSocket server and sets up
     * subscriptions to various topics.
     *
     * @param url The WebSocket server URL
     * @param greetingsTopic The topic for greetings
     * @param clientCountTopic The topic for client count updates
     */
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

                // Subscribe to money updates
                launch {
                    stomp.subscribeText("/topic/money/updates").collect { json ->
                        Log.d("WebSocket", "Received money update: $json")
                        val moneyUpdate = jsonParser.decodeFromString<MoneyUpdateDto>(json)
                        gameViewModel?.onMoneyUpdate(moneyUpdate.playerId, moneyUpdate.amount)
                            ?: Log.e("WebSocket", "ViewModel not yet set - skipping money update.")
                    }
                }

            } catch (e: Exception) {
                connected = false
                Log.e("WebSocket", "Connection failed: ${e.message}")
            }
        }
    }

    /**
     * Sends a message to the specified destination
     *
     * @param destination The destination path
     * @param message The message to send
     */
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

    /**
     * Disconnects from the WebSocket server
     */
    fun disconnect() {
        scope.launch {
            session?.disconnect()
            session = null
            connected = false
            _clientCount.value = 0
            Log.d("WebSocket", "Disconnected")
        }
    }

    /**
     * Joins an existing lobby
     *
     * @param lobbyId The ID of the lobby to join
     * @param playerName The name of the player joining
     */
    fun joinLobby(lobbyId: String, playerName: String) {
        val message = LobbyMessage(
            type = "join",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        send("/app/lobby", json)
    }

    /**
     * Creates a new lobby
     *
     * @param lobbyId The ID for the new lobby
     * @param playerName The name of the player creating the lobby
     */
    fun createLobby(lobbyId: String, playerName: String) {
        val message = LobbyMessage(
            type = "create",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        send("/app/lobby", json)
    }

    /**
     * Starts the game in the specified lobby
     *
     * @param lobbyId The ID of the lobby
     * @param playerName The name of the player starting the game
     */
    fun startGame(lobbyId: String, playerName: String) {
        subscribeToLobby(lobbyId)
        val message = LobbyMessage(
            type = "start",
            playerName = playerName,
            lobbyId = lobbyId
        )
        val json = jsonParser.encodeToString(LobbyMessage.serializer(), message)
        Log.d("WS-START", "Sending start for lobby $lobbyId by $playerName")
        send("/app/lobby", json)
    }

    /**
     * Subscribes to all topics related to a specific lobby
     *
     * @param lobbyId The ID of the lobby to subscribe to
     */
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

            // Subscribe to dice roll results
            launch {
                session!!
                    .subscribeText("/topic/dice/result/$lobbyId")
                    .collect { json ->
                        Log.d("WS-DICE", "Got dice result JSON: $json")
                        val diceResult = jsonParser.decodeFromString<DiceResult>(json)
                        gameViewModel?.receiveDiceResult(diceResult)
                    }
            }

            // Subscribe to money state updates for this lobby
            launch {
                session!!
                    .subscribeText("/topic/money/state/$lobbyId")
                    .collect { json ->
                        Log.d("WebSocket", "Received money state update: $json")
                        val moneyUpdate = jsonParser.decodeFromString<MoneyUpdateDto>(json)
                        gameViewModel?.onMoneyUpdate(moneyUpdate.playerId, moneyUpdate.amount, true)
                            ?: Log.e("WebSocket", "ViewModel not yet set - skipping money state update.")
                    }
            }
        }
    }

    /**
     * Sets the starting money for a player in the lobby.
     *
     * @param lobbyId The ID of the player's lobby
     * @param playerId The ID of the player
     * @param amount The amount of starting money to assign
     */
    fun setStartingMoney(lobbyId: String, playerId: String, amount: Int) {
        val message = """{"lobbyId": "$lobbyId", "playerId": "$playerId", "amount": $amount}"""
        send("/app/money/set", message)
        Log.d("WebSocket", "Starting money set for player $playerId: $amount")
    }

    /**
     * Updates a player's money balance.
     *
     * @param lobbyId The ID of the player's lobby
     * @param playerId The ID of the player
     * @param amount The amount to add (positive) or subtract (negative)
     */
    fun updatePlayerMoney(lobbyId: String, playerId: String, amount: Int) {
        val message = """{"lobbyId": "$lobbyId", "playerId": "$playerId", "amount": $amount}"""
        send("/app/money/update", message)
        Log.d("WebSocket", "Money update for player $playerId: $amount")
    }
}