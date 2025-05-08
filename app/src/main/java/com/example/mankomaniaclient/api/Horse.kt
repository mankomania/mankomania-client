package com.example.mankomaniaclient.api

/**
 * Represents a horse in the horse race game.
 * @property id The unique identifier of the horse.
 * @property name The name of the horse.
 * @property color The color of the horse.
 */
data class Horse(
    val id: Int,
    val name: String,
    val color: String
) {
    /**
     * Converts this horse object to a JSON string using GSON.
     * @return The JSON string representation of this horse.
     */
    fun toJson(): String {
        return com.google.gson.Gson().toJson(this)
    }

    companion object {
        /**
         * Creates a Horse from a JSON string.
         * @param json The JSON string to parse.
         * @return A Horse instance.
         */
        fun fromJson(json: String): Horse {
            return com.google.gson.Gson().fromJson(json, Horse::class.java)
        }
    }
}