package com.example.mankomaniaclient.network
import kotlinx.serialization.Serializable

// fixme they are all messages, ogranize them into packages for maintainability
@Serializable
data class LobbyMessage(
    val type: String,
    val playerName: String,
    val lobbyId: String? = null
)