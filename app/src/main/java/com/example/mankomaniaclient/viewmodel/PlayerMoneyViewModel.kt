package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(private val stompClient: StompClient) : ViewModel() {

    // Pass the stompClient to the service
    private val socketService = PlayerSocketService(stompClient)

    private val _financialState = MutableStateFlow(PlayerFinancialState())
    val financialState: StateFlow<PlayerFinancialState> = _financialState

    init {
        observeMoneyUpdates()
        connectToServer()
    }

    private fun observeMoneyUpdates() {
        viewModelScope.launch {
            socketService.moneyStateFlow.collect { received ->
                _financialState.update {
                    it.copy(
                        bills5000 = received.bills5000,
                        bills10000 = received.bills10000,
                        bills50000 = received.bills50000,
                        bills100000 = received.bills100000
                    )
                }
            }
        }
    }

    private fun connectToServer() {
        viewModelScope.launch {
            socketService.connect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            socketService.disconnect()
        }
    }
}