package com.example.mankomaniaclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel

@Composable
fun StartingMoneyScreen(viewModel: PlayerMoneyViewModel = viewModel()) {
    val state by viewModel.financialState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Starting Money",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        DenominationRow("€5,000", state.bills5000)
        DenominationRow("€10,000", state.bills10000)
        DenominationRow("€50,000", state.bills50000)
        DenominationRow("€100,000", state.bills100000)

        Spacer(modifier = Modifier.height(12.dp))

        val total = state.bills5000 * 5000 +
                state.bills10000 * 10000 +
                state.bills50000 * 50000 +
                state.bills100000 * 100000

        Text(
            text = "Total: €%,d".format(total),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DenominationRow(label: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = "x$quantity", style = MaterialTheme.typography.bodyMedium)
    }
}