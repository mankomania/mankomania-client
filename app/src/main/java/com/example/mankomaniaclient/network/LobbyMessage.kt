package com.example.mankomaniaclient.network
import kotlinx.serialization.Serializable

@Serializable
data class LobbyMessage(
    val type: String,
    val lobbyId: String?,
    val playerName: String
)
