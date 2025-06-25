package com.example.mankomaniaclient.ui

/**
 * Classe che rappresenta lo stato finanziario di un giocatore nel gioco.
 * Contiene informazioni sul denaro attuale del giocatore.
 */
data class PlayerFinancialState(
    val money: Int = 0,
    val isLoading: Boolean = false
)