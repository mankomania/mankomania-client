package com.example.mankomaniaclient.api

import kotlinx.serialization.Serializable

/**
 * Represents a horse selection request in the horse race game.
 * @property playerId The ID of the player making the selection.
 * @property horseId The ID of the horse being selected.
 */
@Serializable
data class HorseSelectionRequest(
    val playerId: String,
    val horseId: Int
)