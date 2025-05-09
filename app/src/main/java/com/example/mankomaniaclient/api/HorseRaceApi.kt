package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HorseRaceApi(private val webSocketService: WebSocketService) {

    private val _horsesStateFlow = MutableStateFlow<List<Horse>>(emptyList())
    val horsesStateFlow: StateFlow<List<Horse>> = _horsesStateFlow

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Sends a horse selection request to the server to place a bet.
     */
    suspend fun sendHorseSelectionRequest(horseSelectionRequest: HorseSelectionRequest) {
        val destination = "/topic/selectHorse"
        val message = json.encodeToString(horseSelectionRequest)
        webSocketService.send(destination, message)
    }

    /**
     * Set up the WebSocket connection only, without collecting flows.
     */
    fun connectWebSocket() {
        webSocketService.connect(
            url = "ws://se2-demo.aau.at:53210/ws",
            greetingsTopic = "/topic/greetings",
            clientCountTopic = "/topic/horses"
        )
    }

    /**
     * Connect to the WebSocket server and subscribe to horse race-related topics.
     */
    suspend fun connectToServer() {
        connectWebSocket()
        webSocketService.clientCount.collect { count ->
            println("Client count updated: $count")
        }
    }

    /**
     * Parse horse data from JSON response.
     */
    fun parseHorseData(json: String): List<Horse> {
        return try {
            this.json.decodeFromString(json)
        } catch (e: Exception) {
            throw e
        }
    }
}