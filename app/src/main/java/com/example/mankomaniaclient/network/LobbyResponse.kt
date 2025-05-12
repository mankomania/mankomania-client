package com.example.mankomaniaclient.network

import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(
    val type: String,
    val lobbyId: String,
    val playerName: String? = null,
    val playerCount: Int? = null,
    val players: List<String>? = null
)