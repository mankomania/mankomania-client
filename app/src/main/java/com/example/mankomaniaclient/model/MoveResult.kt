package com.example.mankomaniaclient.model

import kotlinx.serialization.Serializable

/**
 * @file MoveResult.kt
 * @author eles17
 * @since 2025-05-13
 * @description
 * Represents the result of a player move: where they landed, what type of field it is,
 * and which players are on the same field.
 */
@Serializable
data class MoveResult(
    val newPosition: Int,
    val oldPosition: Int,
    val fieldType: String,
    val fieldDescription: String,
    val playersOnField: List<String>
)