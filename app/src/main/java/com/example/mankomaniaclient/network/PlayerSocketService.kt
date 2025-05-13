package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText

/**
 * Service implementation that handles WebSocket communication
 * with the backend using STOMP protocol.
 */
class PlayerSocketService(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope
) : PlayerSocketServiceInterface {

    private var session: StompSession? = null

    private val _moneyState = MutableStateFlow(
        PlayerFinancialState(
            bills5000 = 0,
            bills10000 = 0,
            bills50000 = 0,
            bills100000 = 0
        )
    )

    override val playerStateFlow: StateFlow<PlayerFinancialState> get() = _moneyState

    /**
     * Connects to the WebSocket and starts collecting updates
     * from the subscribed topic.
     */
    private suspend fun connect(
        url: String = "ws://se2-demo.aau.at:53210/ws",
        topic: String = "/topic/playerMoney"
    ) {
        session = stompClient.connect(url)
        val moneyUpdates = session!!.subscribeText(topic)

        coroutineScope.launch {
            moneyUpdates.collect { json ->
                val update = Json.decodeFromString(PlayerFinancialState.serializer(), json)
                _moneyState.value = update
            }
        }
    }

    /**
     * Sends a player's financial update to the server.
     */
    override suspend fun sendMoneyUpdate(playerId: String) {
        val update = PlayerMoneyUpdate(playerId)
        val json = Json.encodeToString(PlayerMoneyUpdate.serializer(), update)
        session?.sendText("/app/updateMoney", json)
    }

    /**
     * Connects to the WebSocket and subscribes, then sends a money update request.
     */
    override suspend fun connectAndSubscribe(playerId: String) {
        connect()
        sendMoneyUpdate(playerId)
    }

    /**
     * Disconnects the current session from the WebSocket server.
     */
    override suspend fun disconnect() {
        session?.disconnect()
        session = null
    }
}