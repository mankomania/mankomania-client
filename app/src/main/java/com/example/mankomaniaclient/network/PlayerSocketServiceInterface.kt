package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining the contract for WebSocket communication
 * to handle player financial state updates.
 */
interface PlayerSocketServiceInterface {

    /**
     * Connects to the WebSocket server and subscribes to the topic
     * to receive updates on the player's financial state.
     *
     * @param playerId The unique identifier for the player.
     */
    suspend fun connectAndSubscribe(playerId: String)

    /**
     * Sends an update request to the server for the player's money.
     *
     * @param playerId The unique identifier for the player.
     */
    suspend fun sendMoneyUpdate(playerId: String)

    /**
     * Disconnects from the WebSocket server.
     */
    suspend fun disconnect()

    /**
     * A state flow containing the player's current financial state.
     */
    val playerStateFlow: StateFlow<PlayerFinancialState>
}