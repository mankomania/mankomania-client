package com.example.mankomaniaclient.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.stomp.StompSession

class PlayerSocketService(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope
) {

    private var session: StompSession? = null

    private val _moneyStateFlow = MutableStateFlow(PlayerMoneyState())
    val moneyStateFlow: StateFlow<PlayerMoneyState> = _moneyStateFlow

    fun connect() {
        coroutineScope.launch {
            try {
                session = stompClient.connect("ws://se2-demo.aau.at:53210/ws")
                session?.subscribeText("/topic/player-money")?.collect { jsonMessage ->
                    val newState = Json.decodeFromString<PlayerMoneyState>(jsonMessage)
                    _moneyStateFlow.value = newState
                }
                Log.d("WebSocket", "Connected to /topic/player-money")
            } catch (e: Exception) {
                Log.e("WebSocket", "Connection error: ${e.message}", e)
            }
        }
    }

    fun sendMoneyRequest(playerId: String) {
        coroutineScope.launch {
            try {
                val json = """{"playerId":"$playerId"}"""
                session?.sendText("/app/request-money", json)
            } catch (e: Exception) {
                Log.e("WebSocket", "Send error: ${e.message}", e)
            }
        }
    }

    suspend fun disconnect() {
        try {
            session?.disconnect()
        } catch (e: Exception) {
            Log.e("WebSocket", "Disconnect error: ${e.message}", e)
        }
    }
}

@Serializable
data class PlayerMoneyState(
    val bills5000: Int = 0,
    val bills10000: Int = 0,
    val bills50000: Int = 0,
    val bills100000: Int = 0
)