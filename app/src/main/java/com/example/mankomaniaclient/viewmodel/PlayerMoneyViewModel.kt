package com.example.mankomaniaclient.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketServiceInterface
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the player's financial state and handling
 * WebSocket communication with the server. It interacts with the PlayerSocketServiceInterface
 * to send and receive updates.
 *
 * @param socketService Service responsible for WebSocket communication.
 * @param playerId Unique identifier for the player.
 */
class PlayerMoneyViewModel(
    private val socketService: PlayerSocketServiceInterface,
    private val playerId: String
) : ViewModel() {

    // Tag for logging
    companion object {
        private const val LOG_TAG = "PlayerMoneyViewModel"
        private const val LOG_CONNECTED = "Successfully connected and subscribed."
        private const val LOG_CONNECTION_FAILED = "Connection failed"
        private const val LOG_DISCONNECTED = "Disconnected from WebSocket."
        private const val LOG_MONEY_UPDATE_SENT = "Money update sent."
        private const val LOG_MONEY_UPDATE_FAILED = "Failed to send money update"
    }

    // StateFlow for the player's financial state
    val financialState: StateFlow<PlayerFinancialState> = socketService.playerStateFlow

    // StateFlow to track if a connection error occurred
    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    // StateFlow for error messages (null if no error)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // StateFlow to monitor the connection status
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Flag to prevent multiple simultaneous connection attempts
    private var isConnecting = false

    init {
        // Automatically connect to the server on initialization
        connectToServer()
    }

    /**
     * Sets the error message and updates the error state.
     *
     * @param message Error message to display.
     */
    private fun setError(message: String) {
        _errorMessage.value = message
        _hasError.value = true
    }

    /**
     * Clears the current error message and resets the error state.
     */
    private fun clearError() {
        _errorMessage.value = null
        _hasError.value = false
    }

    /**
     * Establishes the WebSocket connection to the server and subscribes to the player's state.
     * Prevents multiple simultaneous connection attempts using the `isConnecting` flag.
     */
    private fun connectToServer() {
        if (isConnecting) return // Avoid multiple connection attempts
        isConnecting = true

        viewModelScope.launch {
            try {
                // Attempt to connect and subscribe to the player's state
                socketService.connectAndSubscribe(playerId)
                _isConnected.value = true // Update connection status
                clearError() // Reset error state on successful connection
                Log.d(LOG_TAG, LOG_CONNECTED)
            } catch (e: Exception) {
                _isConnected.value = false // Update connection status
                setError("$LOG_CONNECTION_FAILED: ${e.message}") // Set error message
                Log.e(LOG_TAG, "$LOG_CONNECTION_FAILED: ${e.message}", e)
            } finally {
                isConnecting = false // Reset the connection flag
            }
        }
    }

    /**
     * Sends a request to the server to update the player's money.
     * Updates the error state if the request fails.
     */
    fun updateMoney() {
        viewModelScope.launch {
            try {
                // Send a money update request to the server
                socketService.sendMoneyUpdate(playerId)
                clearError() // Reset error state on success
                Log.d(LOG_TAG, LOG_MONEY_UPDATE_SENT)
            } catch (e: Exception) {
                setError("$LOG_MONEY_UPDATE_FAILED: ${e.message}") // Set error message
                Log.e(LOG_TAG, "$LOG_MONEY_UPDATE_FAILED: ${e.message}", e)
            }
        }
    }

    /**
     * Retries establishing the WebSocket connection to the server.
     */
    fun retryConnection() {
        connectToServer()
    }

    /**
     * Disconnects from the WebSocket when the ViewModel is cleared.
     * Ensures proper resource cleanup to avoid leaks.
     */
    /**
     * Disconnects from the WebSocket when the ViewModel is cleared.
     * Ensures proper resource cleanup to avoid leaks.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                // Disconnect from the WebSocket
                socketService.disconnect()
                Log.d(LOG_TAG, LOG_DISCONNECTED)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error during disconnect: ${e.message}", e)
            }
        }
    }
}