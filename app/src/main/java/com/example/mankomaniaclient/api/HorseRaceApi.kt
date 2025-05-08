import com.example.mankomaniaclient.api.Horse
import com.example.mankomaniaclient.api.HorseSelectionRequest
import com.example.mankomaniaclient.network.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HorseRaceApi(private val webSocketService: WebSocketService) {

    // StateFlow to hold the list of horses received from the server
    private val _horsesStateFlow = MutableStateFlow<List<Horse>>(emptyList())
    val horsesStateFlow: StateFlow<List<Horse>> = _horsesStateFlow

    /**
     * Sends a horse selection request to the server to place a bet.
     * @param horseSelectionRequest the request containing the horse selected by the player.
     */
    suspend fun sendHorseSelectionRequest(horseSelectionRequest: HorseSelectionRequest) {
        val destination = "/topic/selectHorse"
        val message = Json.encodeToString(horseSelectionRequest) // Convert to JSON string

        // Send the message to the server to register the horse selection
        webSocketService.send(destination, message)
    }

    /**
     * Connect to the WebSocket server and subscribe to horse race-related topics.
     */
    suspend fun connectToServer() {
        // Connect to WebSocket and subscribe to topics
        webSocketService.connect(
            url = "ws://your-websocket-server-url", // Replace with actual server URL
            greetingsTopic = "/topic/greetings", // Example topic, change as needed
            clientCountTopic = "/topic/horses" // Example topic, change as needed
        )

        // Collect and update the list of horses (or any relevant data)
        webSocketService.clientCount.collect { horseData ->
            // If horseData is a list, this will work
            // Assuming `horseData` is a List<Horse> from the WebSocket server
            @Suppress("UNCHECKED_CAST")
            _horsesStateFlow.value = horseData as List<Horse> // Add casting to ensure type safety
        }
    }
}