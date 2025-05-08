package com.example.mankomaniaclient.api

data class HorseSelectionRequest(
    val playerId: String,
    val horseId: Int
) {
    fun toJson() {
        TODO("Not yet implemented")
    }
}
