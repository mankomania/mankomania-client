package com.example.mankomaniaclient.network
import kotlinx.serialization.Serializable

@Serializable
data class LobbyMessage(
    val type: String,
    val playerName: String,
    val lobbyId: String? = null,
    val boardSize: Int? = null
)