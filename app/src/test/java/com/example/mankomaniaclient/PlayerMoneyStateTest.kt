package com.example.mankomaniaclient

import com.example.mankomaniaclient.ui.model.MoneyDenomination
import com.example.mankomaniaclient.ui.model.PlayerMoneyState
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerMoneyStateTest {

    @Test
    fun totalAmount_calculatesCorrectTotal() {
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
    fun emptyDenominations_returnZeroTotal() {
        val state = PlayerMoneyState(emptyList())
        assertEquals(0, state.totalAmount())
    }
}