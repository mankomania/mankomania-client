package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.PlayerFinancialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class PlayerSocketService : PlayerSocketServiceInterface {

    private val _playerStateFlow = MutableStateFlow(PlayerFinancialState())
    override val playerStateFlow: StateFlow<PlayerFinancialState> = _playerStateFlow

    override suspend fun connectAndSubscribe(playerId: String) {
        try {
            _playerStateFlow.value = _playerStateFlow.value.copy(isLoading = true)

            val client = StompClient(OkHttpWebSocketClient())
            val session = try {
                client.connect("ws://YOUR_SERVER_URL/ws")
            } catch (e: Exception) {
                throw Exception("Impossibile connettersi al server", e)
            }

            session.subscribeText("/topic/player/$playerId/money").collect { message ->
                val moneyValue = message.toIntOrNull() ?: return@collect
                _playerStateFlow.value = PlayerFinancialState(money = moneyValue, isLoading = false)
            }
        } catch (e: Exception) {
            _playerStateFlow.value = _playerStateFlow.value.copy(isLoading = false)
            throw e
        }
    }
}