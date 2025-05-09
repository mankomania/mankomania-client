package com.example.mankomaniaclient.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Represents a horse in the horse race game.
 * @property id The unique identifier of the horse.
 * @property name The name of the horse.
 * @property color The color of the horse.
 */
@Serializable
data class Horse(
    val id: Int,
    val name: String,
    val color: String? = null
) {
    /**
     * Converts this horse object to a JSON string using Kotlinx Serialization.
     * @return The JSON string representation of this horse.
     */
    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        /**
         * Creates a Horse from a JSON string.
         * @param json The JSON string to parse.
         * @return A Horse instance.
         */
        fun fromJson(json: String): Horse {
            return Json.decodeFromString(serializer(), json)
        }
    }
}