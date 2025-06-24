package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.PlayerFinancialState
import kotlinx.coroutines.flow.StateFlow

/**
 * Interfaccia per il servizio che gestisce la connessione WebSocket
 * per gli aggiornamenti finanziari dei giocatori.
 */
interface PlayerSocketServiceInterface {
    /**
     * Flow che emette gli aggiornamenti dello stato finanziario del giocatore
     */
    val playerStateFlow: StateFlow<PlayerFinancialState>

    /**
     * Stabilisce una connessione WebSocket e si iscrive agli aggiornamenti
     * per il giocatore specificato
     *
     * @param playerId ID del giocatore di cui ricevere gli aggiornamenti
     */
    suspend fun connectAndSubscribe(playerId: String)
}
