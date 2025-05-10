package com.example.mankomaniaclient.ui.model
import kotlinx.serialization.Serializable

@Serializable
data class MoneyDenomination(
    val value: Int,
    val quantity: Int
) {
    fun totalAmount(): Int = value * quantity
}