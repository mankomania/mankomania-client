package com.example.mankomaniaclient.api

import com.google.gson.Gson

data class HorseSelectionRequest(
    val playerId: String,
    val horseId: Int
) {
    companion object {
        private val gson = Gson()

        /**
         * Deserializes a JSON string into a HorseSelectionRequest object
         */
        fun fromJson(json: String): HorseSelectionRequest {
            return gson.fromJson(json, HorseSelectionRequest::class.java)
        }
    }

    /**
     * Converts this request object to a JSON string using Gson.
     * @return The JSON string representation of this request.
     */
    fun toJson(): String {
        return Gson().toJson(this)
    }
}