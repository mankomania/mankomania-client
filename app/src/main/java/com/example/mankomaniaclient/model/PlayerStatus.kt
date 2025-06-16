package com.example.mankomaniaclient.model

/**
 *
 * @author eles17
 * @since 14.5.2025
 * @description
 * Implement real-time status updates for each player using WebSocket messages and reflect them in the UI.
 */
data class PlayerStatus(
    val name: String,
    val position: Int,
    val balance: Int,
    val money: Map<Int, Int>,
    val isTurn: Boolean
)