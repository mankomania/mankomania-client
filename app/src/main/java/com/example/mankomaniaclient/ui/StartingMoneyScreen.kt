package com.example.mankomaniaclient.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModelFactory
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.network.PlayerSocketServiceInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun StartingMoneyScreen(playerId: String) {
    // Create a CoroutineScope tied to the composable's lifecycle
    val coroutineScope = rememberCoroutineScope()

    // Create an instance of PlayerSocketService
    val socketService: PlayerSocketServiceInterface = remember {
        PlayerSocketService(
            stompClient = StompClient(OkHttpWebSocketClient()),
            coroutineScope = coroutineScope // Use the remembered coroutine scope
        )
    }

    // Create a ViewModelFactory and initialize the ViewModel
    val factory = remember {
        PlayerMoneyViewModelFactory(
            socketService = socketService,
            playerId = playerId
        )
    }
    val viewModel: PlayerMoneyViewModel = viewModel(factory = factory)

    // Collect the UI state and error state from the ViewModel
    val state by viewModel.financialState.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    // Calculate the total amount dynamically
    val totalAmount = remember(state) {
        state.bills5000 * 5000 +
                state.bills10000 * 10_000 +
                state.bills50000 * 50_000 +
                state.bills100000 * 100_000
    }

    // Format the total amount based on the locale
    val formattedTotalAmount = remember(totalAmount) {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).format(totalAmount)
    }

    // Scaffold UI to provide a top bar and content
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Starting Money 💰") })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the player ID
            Text(
                text = "Player: $playerId",
                fontSize = 18.sp,
                color = Color.Gray
            )

            // Show error card if an error exists
            if (hasError) {
                ErrorCard(viewModel = viewModel)
            }

            // Display denomination boxes in rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DenominationBox("€5,000", state.bills5000, Color(0xFFE0F7FA))
                DenominationBox("€10,000", state.bills10000, Color(0xFFD1C4E9))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DenominationBox("€50,000", state.bills50000, Color(0xFFFFF59D))
                DenominationBox("€100,000", state.bills100000, Color(0xFFFFCCBC))
            }

            // Display the total amount
            Text(
                text = "Total: $formattedTotalAmount",
                fontSize = 22.sp,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.titleMedium
            )

            // Button to refresh money
            Button(
                onClick = { viewModel.updateMoney() },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Money")
            }
        }
    }

    // Cancel the coroutine scope when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }
}


@Composable
fun ErrorCard(viewModel: PlayerMoneyViewModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⚠️ Connection failed. Please check your network and try again.",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { viewModel.retryConnection() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun DenominationBox(
    denominationText: String,
    count: Int,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Euro,
            contentDescription = "Euro denomination icon",
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = denominationText,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "x$count",
            fontSize = 20.sp,
            color = Color.DarkGray
        )
    }
}