

/*
 * @file GameViewModel.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description ViewModel that holds the current game state:
 *              the list of board cells and the list of players.
 */

package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mankomaniaclient.network.CellDto
import com.example.mankomaniaclient.network.GameStateDto
import com.example.mankomaniaclient.network.PlayerDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {
    private val _board   = MutableStateFlow<List<CellDto>>(emptyList())
    val board: StateFlow<List<CellDto>> = _board

    private val _players = MutableStateFlow<List<PlayerDto>>(emptyList())
    val players: StateFlow<List<PlayerDto>> = _players

    /** Called by WebSocketService when a new GameStateDto arrives */
    fun onGameState(state: GameStateDto) {
        _board.value   = state.board
        _players.value = state.players
    }

    fun setPlayers(names: List<String>) {
        _players.value = names.mapIndexed { index, name -> PlayerDto(name, index) }
    }
}
