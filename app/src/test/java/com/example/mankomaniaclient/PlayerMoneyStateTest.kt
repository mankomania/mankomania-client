package com.example.mankomaniaclient

import com.example.mankomaniaclient.ui.model.MoneyDenomination
import com.example.mankomaniaclient.ui.model.PlayerMoneyState
import junit.framework.TestCase.assertEquals
import org.junit.Test


class PlayerMoneyStateTest {

    @Test
    fun `totalAmount calculates correct total`() {
        val denominations = listOf(
            MoneyDenomination(5000, 10),
            MoneyDenomination(10000, 5),
            MoneyDenomination(50000, 4),
            MoneyDenomination(100000, 7)
        )
        val state = PlayerMoneyState(denominations)
        assertEquals(1_000_000, state.totalAmount())
    }

    @Test
    fun `empty denominations return 0 total`() {
        val state = PlayerMoneyState(emptyList())
        assertEquals(0, state.totalAmount())
    }
}