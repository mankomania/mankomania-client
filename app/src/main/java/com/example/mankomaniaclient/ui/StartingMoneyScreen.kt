package com.example.mankomaniaclient.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel

@Composable
fun StartingMoneyScreen(viewModel: PlayerMoneyViewModel = viewModel()) {
    // Collect the state safely with a default empty state
    val state by viewModel.financialState.collectAsState()

    Column {
        // Use safe-call operator (?.) to safely access properties

        DenominationRow("€5,000", state?.bills5000 ?: 0, Color(0xFFE0F7FA))


        DenominationRow("€10,000", state?.bills10000 ?: 0, Color(0xFFD1C4E9))


        DenominationRow("€50,000", state?.bills50000 ?: 0, Color(0xFFFFF59D))

        DenominationRow("€100,000", state?.bills100000 ?: 0, Color(0xFFFFCCBC))
    }
}

@Composable
fun DenominationRow(x0: String, x1: Int, x2: Color) {
    TODO("Not yet implemented")
}