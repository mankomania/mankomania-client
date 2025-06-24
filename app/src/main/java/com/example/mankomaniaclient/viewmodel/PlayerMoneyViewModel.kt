package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketServiceInterface
import com.example.mankomaniaclient.ui.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simple ViewModel to manage player's money state.
 * Connects to WebSocket service and exposes the current money state.
 */
class PlayerMoneyViewModel(
    private val socketService: PlayerSocketServiceInterface,
    private val playerId: String
) : ViewModel() {

    // StateFlow to hold the player's financial state
    private val _financialState = kotlinx.coroutines.flow.MutableStateFlow(PlayerFinancialState())
    val financialState: StateFlow<PlayerFinancialState> = _financialState

    // StateFlow for connection errors (simple boolean flag)
    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    init {
        // Start connection and subscription on init
        connect()
    }

    private fun connect() {
        viewModelScope.launch {
            try {
                // Connect and subscribe to player updates
                socketService.connectAndSubscribe(playerId)

                // Collect updates from the socket service and update the state
                socketService.playerStateFlow.collect { state ->
                    _financialState.value = state
                    _hasError.value = false
                }
            } catch (e: Exception) {
                _hasError.value = true
            }
        }
    }

    fun retryConnection() {
        connect()
    }
}