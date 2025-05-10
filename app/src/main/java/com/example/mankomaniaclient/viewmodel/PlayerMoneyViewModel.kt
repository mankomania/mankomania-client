package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(
    private val stompClient: StompClient,
    private val externalScope: CoroutineScope
) : ViewModel() {

    // Initialize PlayerSocketService with the stompClient and externalScope
    private val socketService = PlayerSocketService(stompClient, externalScope)

    // Player ID (you might want to make this configurable)
    private val playerId = "player1"

    // Get the state flow from the service
    val financialState: StateFlow<PlayerFinancialState?> = socketService.playerStateFlow

    init {
        connectToServer()
    }

    private fun connectToServer() {
        viewModelScope.launch {
            socketService.connectAndSubscribe(playerId)
        }
    }

    // Function to update player's money
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