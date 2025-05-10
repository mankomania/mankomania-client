package com.example.mankomaniaclient.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerMoneyUpdate(
    val playerId: String
)