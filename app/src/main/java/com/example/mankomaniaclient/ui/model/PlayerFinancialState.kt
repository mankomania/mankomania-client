package com.example.mankomaniaclient.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerFinancialState(
    val bills5000: Int = 0,
    val bills10000: Int = 0,
    val bills50000: Int = 0,
    val bills100000: Int = 0
)