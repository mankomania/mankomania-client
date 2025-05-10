package com.example.mankomaniaclient.ui.model

import kotlinx.serialization.Serializable
@Serializable
data class PlayerMoneyState(
    val denominations: List<MoneyDenomination>
) {
    fun totalAmount(): Int = denominations.sumOf { it.totalAmount() }
}