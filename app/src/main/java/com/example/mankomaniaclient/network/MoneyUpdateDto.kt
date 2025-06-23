package com.example.mankomaniaclient.network

import kotlinx.serialization.Serializable

@Serializable
data class MoneyUpdateDto(
    val playerId: String,
    val amount: Int,
    val lobbyId: String
)