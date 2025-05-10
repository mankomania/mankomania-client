package com.example.mankomaniaclient.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        DenominationRow("€5,000", state?.bills5000 ?: , Color(0xFFE0F7FA))
        DenominationRow("€10,000", state.bills10000, Color(0xFFD1C4E9))
        DenominationRow("€50,000", state.bills50000, Color(0xFFFFF59D))
        DenominationRow("€100,000", state.bills100000, Color(0xFFFFCCBC))

        Spacer(modifier = Modifier.height(16.dp))

        val total = state.bills5000 * 5000 +
                state.bills10000 * 10000 +
                state.bills50000 * 50000 +
                state.bills100000 * 100000

        Text(
            text = "Total: €${total.formatWithSeparator()}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun DenominationRow(label: String, count: Int, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "x$count", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun Int.formatWithSeparator(): String = "%,d".format(this).replace(',', '.')