package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mankomaniaclient.network.PlayerSocketServiceInterface
import kotlinx.coroutines.CoroutineScope

/**
 * Factory class to create an instance of PlayerMoneyViewModel with the required dependencies.
 *
 * @param socketService The PlayerSocketServiceInterface used for WebSocket communication.
 * @param playerId The unique identifier for the player.
 */
class PlayerMoneyViewModelFactory(
    private val socketService: PlayerSocketServiceInterface,
    private val playerId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerMoneyViewModel::class.java)) {
            return PlayerMoneyViewModel(socketService, playerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}