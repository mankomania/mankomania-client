package com.example.mankomaniaclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel

@Composable
fun PlayerMoneyScreen(viewModel: PlayerMoneyViewModel) {
    val financialState by viewModel.financialState.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (financialState.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Actual money:",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "${financialState.money} €",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        if (hasError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connection errror",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = { viewModel.retryConnection() }) {
                Text("Try again!")
            }
        }
    }
}