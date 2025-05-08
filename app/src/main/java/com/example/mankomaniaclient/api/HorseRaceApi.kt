package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HorseRaceApi(private val webSocketService: WebSocketService) {

    // StateFlow to hold the list of horses received from the server
    private val _horsesStateFlow = MutableStateFlow<List<Horse>>(emptyList())
    val horsesStateFlow: StateFlow<List<Horse>> = _horsesStateFlow

    // Gson instance for JSON serialization and deserialization
    private val gson = Gson()

    /**
     * Sends a horse selection request to the server to place a bet.
     * @param horseSelectionRequest the request containing the horse selected by the player.
     */
    suspend fun sendHorseSelectionRequest(horseSelectionRequest: HorseSelectionRequest) {
        val destination = "/topic/selectHorse"
        val message = gson.toJson(horseSelectionRequest) // Use Gson to convert to JSON string

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
        // Use Gson to parse JSON array into list of Horse objects
        return gson.fromJson(json, Array<Horse>::class.java).toList()
    }
}