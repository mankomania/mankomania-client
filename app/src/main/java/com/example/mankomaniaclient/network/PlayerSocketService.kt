package com.example.mankomaniaclient.network


import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job // Import Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText

class PlayerSocketService(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope // This scope is for launching collection
) {

    private var session: StompSession? = null
    private var moneySubscriptionJob: Job? = null // To hold the subscription job

    private val _moneyState = MutableStateFlow(
        PlayerFinancialState(
            bills5000 = 0,
            bills10000 = 0,
            bills50000 = 0,
            bills100000 = 0
        )
    )
    val playerStateFlow: StateFlow<PlayerFinancialState> get() = _moneyState

    suspend fun connect(
        url: String = "ws://se2-demo.aau.at:53210/ws",
        topic: String = "/topic/playerMoney"
    ) {
        // Cancel any existing job before starting a new one or if reconnecting
        moneySubscriptionJob?.cancel()

        session = stompClient.connect(url)
        val moneyUpdates = session!!.subscribeText(topic)

        moneySubscriptionJob = coroutineScope.launch { // Assign the launched job
            moneyUpdates.collect { json ->
                try {
                    val update = Json.decodeFromString(PlayerFinancialState.serializer(), json)
                    _moneyState.value = update
                } catch (e: Exception) {
                    // Optional: Log or handle deserialization errors
                    // e.g., println("Error deserializing PlayerFinancialState: ${e.message}")
                }
            }
        }
    }

    suspend fun sendMoneyUpdate(playerId: String) {
        val update = PlayerMoneyUpdate(playerId)
        val json = Json.encodeToString(PlayerMoneyUpdate.serializer(), update)
        session?.sendText("/app/updateMoney", json)
    }

    suspend fun connectAndSubscribe(playerId: String) {
        connect() // Defaults will be used for url and topic
        sendMoneyUpdate(playerId)
    }

    suspend fun disconnect() {
        moneySubscriptionJob?.cancel() // Cancel the subscription job
        moneySubscriptionJob = null   // Clear the reference
        session?.disconnect()
        session = null
    }
}