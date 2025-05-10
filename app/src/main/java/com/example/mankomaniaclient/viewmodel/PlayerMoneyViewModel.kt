package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import android.util.Log

class PlayerMoneyViewModel(
    private val stompClient: StompClient,
    private val playerId: String
) : ViewModel() {

    private val socketService = PlayerSocketService(stompClient, viewModelScope)

    // Exposed state to observe the current financial data of the player
    val financialState: StateFlow<PlayerFinancialState> = socketService.playerStateFlow

    init {
        connectToServer()
    }

    /**
     * Connects to the WebSocket server and subscribes to the playerMoney topic.
     * If the connection fails, it catches the exception and logs it instead of crashing the UI.
     */
    private fun connectToServer() {
        viewModelScope.launch {
            try {
                socketService.connectAndSubscribe(playerId)
            } catch (e: Exception) {
                Log.e("PlayerMoneyViewModel", "WebSocket connection failed: ${e.message}", e)
            }
        }
    }

    /**
     * Sends a money update to the backend.
     */
    fun updateMoney() {
        viewModelScope.launch {
            try {
                socketService.sendMoneyUpdate(playerId)
            } catch (e: Exception) {
                Log.e("PlayerMoneyViewModel", "Failed to send money update: ${e.message}", e)
            }
        }
    }

    /**
     * Ensures WebSocket disconnection when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                socketService.disconnect()
            } catch (e: Exception) {
                Log.e("PlayerMoneyViewModel", "WebSocket disconnection failed: ${e.message}", e)
            }
        }
    }
}