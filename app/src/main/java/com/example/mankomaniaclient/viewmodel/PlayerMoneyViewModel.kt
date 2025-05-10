package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(private val stompClient: StompClient) : ViewModel() {

    // Define the player ID
    private val playerId = "player1"

    // Initialize the socket service with stompClient and viewModelScope
    // Pass viewModelScope directly, not as a function call
    private val socketService = PlayerSocketService(stompClient, viewModelScope)

    // Get the financial state from the service
    val financialState: StateFlow<PlayerFinancialState?> = socketService.playerStateFlow

    init {
        connectToServer()
    }

    private fun connectToServer() {
        viewModelScope.launch {
            socketService.connectAndSubscribe(playerId)
        }
    }

    fun updateMoney(updatedState: PlayerFinancialState) {
        viewModelScope.launch {
            socketService.sendMoneyUpdate(playerId, updatedState)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            socketService.disconnect()
        }
    }
}