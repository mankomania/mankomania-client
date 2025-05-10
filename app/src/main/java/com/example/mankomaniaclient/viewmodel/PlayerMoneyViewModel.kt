package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModel(
    private val stompClient: StompClient,
    private val playerId: String
) : ViewModel() {

    private val socketService = PlayerSocketService(stompClient, viewModelScope)

    val financialState: StateFlow<PlayerFinancialState> = socketService.playerStateFlow

    init {
        connectToServer()
    }

    private fun connectToServer() {
        viewModelScope.launch {
            socketService.connectAndSubscribe(playerId)
        }
    }

    fun updateMoney() {
        viewModelScope.launch {
            socketService.sendMoneyUpdate(playerId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            socketService.disconnect()
        }
    }
}