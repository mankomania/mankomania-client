package com.example.mankomaniaclient.network

import android.util.Log
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText

class PlayerSocketService(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope
) {

    private var session: StompSession? = null
    private val json = Json { ignoreUnknownKeys = true }

    private val _playerStateFlow = MutableStateFlow<PlayerFinancialState?>(null)
    val playerStateFlow = _playerStateFlow.asStateFlow()

    suspend fun connectAndSubscribe(playerId: String) {
        try {
            session = stompClient.connect("ws://se2-demo.aau.at:53210/ws")

            val topic = "/topic/player/$playerId"
            session?.subscribeText(topic)?.onEach { message ->
                try {
                    val state = json.decodeFromString(PlayerFinancialState.serializer(), message)
                    _playerStateFlow.value = state
                } catch (e: Exception) {
                    Log.e("WebSocket", "Deserialization error: ${e.message}")
                }
            }?.launchIn(coroutineScope)

        } catch (e: Exception) {
            Log.e("WebSocket", "Connection error: ${e.message}")
        }
    }

    suspend fun sendMoneyUpdate(playerId: String, updatedState: PlayerFinancialState) {
        val destination = "/app/player/$playerId"
        val message = json.encodeToString(PlayerFinancialState.serializer(), updatedState)
        session?.sendText(destination, message)
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
    }
}