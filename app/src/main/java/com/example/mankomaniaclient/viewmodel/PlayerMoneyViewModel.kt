package com.example.mankomaniaclient.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(
    private val stompClient: StompClient,
    private val playerId: String
) : ViewModel() {

    private val socketService = PlayerSocketService(stompClient, viewModelScope)

    val financialState: StateFlow<PlayerFinancialState> = socketService.playerStateFlow

    // Add this connection error state
    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    init {
        connectToServer()

        // Optional: Debug logging of incoming financial state updates
        viewModelScope.launch {
            financialState.collect { state ->
                Log.d("PlayerMoneyViewModel", "Received updated financial state: $state")
            }
        }
    }

    /**
     * Establishes the WebSocket connection and subscribes to the player's state.
     * The call is wrapped in try-catch to prevent crashing the UI in case of connection errors.
     */
    private fun connectToServer() {
        viewModelScope.launch {
            try {
                socketService.connectAndSubscribe(playerId)
                _hasError.value = false // Reset error state on successful connection
                Log.d("PlayerMoneyViewModel", "Successfully connected and subscribed.")
            } catch (e: Exception) {
                _hasError.value = true // Set error state on connection failure
                Log.e("PlayerMoneyViewModel", "Connection failed: ${e.message}", e)
            }
        }
    }

    /**
     * Sends a money update request to the server for this player.
     */
    fun updateMoney() {
        viewModelScope.launch {
            try {
                socketService.sendMoneyUpdate(playerId)
                _hasError.value = false // Reset error state on success
                Log.d("PlayerMoneyViewModel", "Money update sent.")
            } catch (e: Exception) {
                _hasError.value = true // Set error state on failure
                Log.e("PlayerMoneyViewModel", "Failed to send money update: ${e.message}", e)
            }
        }
    }

    /**
     * Retry connecting to the server
     */
    fun retryConnection() {
        connectToServer()
    }

    /**
     * Disconnects from the server when the ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                socketService.disconnect()
                Log.d("PlayerMoneyViewModel", "Disconnected from WebSocket.")
            } catch (e: Exception) {
                Log.e("PlayerMoneyViewModel", "Error during disconnect: ${e.message}", e)
            }
        }
    }
}