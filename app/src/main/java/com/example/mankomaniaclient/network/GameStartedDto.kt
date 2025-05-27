/**
 * @file GameStartedDto.kt
 * @author Angela Drucks
 * @since 2025-05-27
 * @description DTO sent by the server when a game session starts, containing
 *              the game ID and the list of starting positions for every player.
 */

package com.example.mankomaniaclient.network

import kotlinx.serialization.Serializable

@Serializable
data class GameStartedDto(
    val gameId: String,
    val startPositions: List<Int>
)
