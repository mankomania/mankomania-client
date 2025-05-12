package com.example.mankomaniaclient.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoardScreen(playerNames: List<String>, lobbyId: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = playerNames.getOrNull(0) ?: "",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(1) ?: "",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(2) ?: "",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        )

        Text(
            text = playerNames.getOrNull(3) ?: "",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}


