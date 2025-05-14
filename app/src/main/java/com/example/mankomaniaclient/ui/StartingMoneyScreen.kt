package com.example.mankomaniaclient.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.graphics.Shape
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModelFactory
import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.network.PlayerSocketServiceInterface
import kotlinx.coroutines.cancel
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun StartingMoneyScreen(playerId: String) {
    val coroutineScope = rememberCoroutineScope()
    val socketService: PlayerSocketServiceInterface = remember {
        PlayerSocketService(
            stompClient = StompClient(OkHttpWebSocketClient()),
            coroutineScope = coroutineScope
        )
    }

    val factory = remember {
        PlayerMoneyViewModelFactory(
            socketService = socketService,
            playerId = playerId
        )
    }
    val viewModel: PlayerMoneyViewModel = viewModel(factory = factory)

    val state by viewModel.financialState.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    val totalAmount = remember(state) {
        state.bills5000 * 5000 +
                state.bills10000 * 10_000 +
                state.bills50000 * 50_000 +
                state.bills100000 * 100_000
    }

    val formattedTotalAmount = remember(totalAmount) {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).format(totalAmount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Starting Money 💰") },
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
            )
        }
    ) { innerPadding ->
        // Apply system window insets to avoid overlapping with system bars
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Player: $playerId",
                fontSize = 18.sp,
                color = Color.Gray
            )

            if (hasError) {
                ErrorCard(viewModel = viewModel)
            }

            // Use a responsive grid layout for denomination boxes
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { DenominationBox("€5,000", state.bills5000, Color(0xFFE0F7FA)) }
                item { DenominationBox("€10,000", state.bills10000, Color(0xFFD1C4E9)) }
                item { DenominationBox("€50,000", state.bills50000, Color(0xFFFFF59D)) }
                item { DenominationBox("€100,000", state.bills100000, Color(0xFFFFCCBC)) }
            }

            Text(
                text = "Total: $formattedTotalAmount",
                fontSize = 22.sp,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.titleMedium
            )

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
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars)
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