package com.example.mankomaniaclient

import android.content.Intent

/**
 * Utility function to extract the playerId from an Intent or return a default value.
 */
fun extractPlayerId(intent: Intent?): String {
    return intent?.getStringExtra("playerId") ?: "default-player"
}