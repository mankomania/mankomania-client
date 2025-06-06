

/**
 * @file GameStateDto.kt
 * @author Angela Drucks
 * @since 2025-05-08
 * @description Defines the data transfer objects for the full game state
 *              received from the server over WebSocket.
 *
 * @property players List of players with their current positions.
 * @property board   List of board cells including index and branch flag.
 */

package com.example.mankomaniaclient.network

import kotlinx.serialization.Serializable

@Serializable
data class CellDto(
    val index: Int,
    val hasBranch: Boolean,
    val type: String? = null
)

@Serializable
data class PlayerDto(
    val name: String,
    val position: Int
)

@Serializable
data class GameStateDto(
    val players: List<PlayerDto>,
    val board:   List<CellDto>
)


