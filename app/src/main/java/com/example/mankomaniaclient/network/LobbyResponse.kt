package com.example.mankomaniaclient.network
import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(
    val type: String,
    val lobbyId: String,
    val playerCount: Int,
    val players: List<String>? = null
)