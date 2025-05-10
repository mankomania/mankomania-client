package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import org.hildan.krossbow.stomp.StompClient

class PlayerMoneyViewModelFactory(
    private val stompClient: StompClient,
    private val coroutineScope: CoroutineScope
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerMoneyViewModel::class.java)) {
            return PlayerMoneyViewModel(stompClient, coroutineScope) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}