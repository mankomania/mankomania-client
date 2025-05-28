/**
 * @file DiceMoveResultDto.kt
 * @author Angela Drucks
 * @since 2025-05-27
 * @description Combines dice roll outcome and resulting move information that
 *              the server broadcasts after a player action.
 */
package com.example.mankomaniaclient.network

import kotlinx.serialization.Serializable

@Serializable
data class DiceMoveResultDto(
    val playerId: String,
    val die1: Int,
    val die2: Int,
    val sum: Int,
    val fieldIndex: Int,
    val fieldType: String,
    val fieldDescription: String,
    val playersOnField: List<String>
)
