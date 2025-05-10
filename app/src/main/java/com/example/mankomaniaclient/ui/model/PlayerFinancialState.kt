package com.example.mankomaniaclient.ui.model

data class PlayerFinancialState(
    val bills5000: Int,
    val bills10000: Int,
    val bills50000: Int,
    val bills100000: Int
) {
    val total: Int
        get() = bills5000 * 5000 +
                bills10000 * 10000 +
                bills50000 * 50000 +
                bills100000 * 100000
}