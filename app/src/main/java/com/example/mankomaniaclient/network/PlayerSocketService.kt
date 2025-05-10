package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(private val stompClient: StompClient) : ViewModel() {

    // Initialize PlayerSocketService with the stompClient and viewModelScope
    private val socketService = PlayerSocketService(stompClient, viewModelScope)

    // Player ID (you might want to make this configurable)
    private val playerId = "player1"

    private val _financialState = MutableStateFlow(PlayerFinancialState())
    val financialState: StateFlow<PlayerFinancialState> = _financialState

    init {
        connectToServer()
        observeMoneyUpdates()
    }

    private fun observeMoneyUpdates() {
        viewModelScope.launch {
            socketService.playerStateFlow.collectLatest { playerState ->
                // Only update if we received a non-null state
                playerState?.let { state ->
                    _financialState.value = state
                }
            }
        }
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