package com.example.mankomaniaclient.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * # PlayerCharacterView
 *
 * Stellt den eigenen Spielcharakter dar.
 *
 * @author eles17
 * @since 14.05.2025
 * @description Composable-Funktion für die visuelle Darstellung des Spielers.
 * @param playerIndex The index of the player (0–3), determines color.
 */
@Composable
fun PlayerCharacterView(playerIndex: Int) {
    val color = when (playerIndex) {
        0 -> Color.Red
        1 -> Color.Blue
        2 -> Color.Green
        3 -> Color.Yellow
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color)
            .zIndex(1f) // Ensure figure draws above the cell
            .testTag("PlayerFigure") // Added for testing
    )
}