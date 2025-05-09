package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class HorseRaceApi(private val webSocketService: WebSocketService) {

    // StateFlow to hold the list of horses received from the server
    private val _horsesStateFlow = MutableStateFlow<List<Horse>>(emptyList())
    val horsesStateFlow: StateFlow<List<Horse>> = _horsesStateFlow

    // Json formatter with lenient configuration for more forgiving parsing
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Sends a horse selection request to the server to place a bet.
     * @param horseSelectionRequest the request containing the horse selected by the player.
     */
    suspend fun sendHorseSelectionRequest(horseSelectionRequest: HorseSelectionRequest) {
        val destination = "/topic/selectHorse"
        val message = Json.encodeToString(HorseSelectionRequest.serializer, horseSelectionRequest)

        // Send the message to the server to register the horse selection
        webSocketService.send(destination, message)
    }

    /**
     * Set up the WebSocket connection only, without collecting flows.
     * Extracted for better testability.
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
        // Connect to WebSocket and subscribe to topics
        connectWebSocket()

        webSocketService.clientCount.collect { count ->
            println("Client count updated: $count")
        }
    }

    /**
     * Parse horse data from JSON response
     * @param json The JSON string containing horse data
     * @return List of Horse objects
     */
    fun parseHorseData(json: String): List<Horse> {
        return try {
            // Use Kotlinx serialization to parse JSON array into list of Horse objects
            Json.decodeFromString<List<Horse>>(json)
        } catch (e: Exception) {
            throw e // Re-throw to maintain same error behavior as original
        }
    }
}