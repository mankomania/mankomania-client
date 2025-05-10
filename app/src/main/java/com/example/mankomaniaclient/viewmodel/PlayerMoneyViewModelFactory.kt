package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModelFactory(
    private val stompClient: StompClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerMoneyViewModel::class.java)) {
            // We're only passing stompClient now, since that's what the constructor expects
            return PlayerMoneyViewModel(stompClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}