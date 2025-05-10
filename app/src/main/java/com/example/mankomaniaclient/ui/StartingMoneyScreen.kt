package com.example.mankomaniaclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModel
import com.example.mankomaniaclient.viewmodel.PlayerMoneyViewModelFactory
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

@Composable
fun StartingMoneyScreen(playerId: String) {
    val factory = remember {
        PlayerMoneyViewModelFactory(
            stompClient = StompClient(OkHttpWebSocketClient()),
            playerId = playerId
        )
    }

    val viewModel: PlayerMoneyViewModel = viewModel(factory = factory)
    val state by viewModel.financialState.collectAsState()

    val totalAmount = remember(state) {
        state.bills5000 * 5000 +
                state.bills10000 * 10_000 +
                state.bills50000 * 50_000 +
                state.bills100000 * 100_000
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Pass the modifier with weight directly from the parent Row
            DenominationBox(
                label = "€5,000",
                count = state.bills5000,
                color = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f)
            )
            DenominationBox(
                label = "€10,000",
                count = state.bills10000,
                color = Color(0xFFD1C4E9),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DenominationBox(
                label = "€50,000",
                count = state.bills50000,
                color = Color(0xFFFFF59D),
                modifier = Modifier.weight(1f)
            )
            DenominationBox(
                label = "€100,000",
                count = state.bills100000,
                color = Color(0xFFFFCCBC),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Total: €${"%,d".format(totalAmount)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF388E3C),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DenominationBox(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier // Add a modifier parameter with default value
) {
    Box(
        modifier = modifier // Use the modifier passed from the parent
            .padding(8.dp)
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "x$count",
                fontSize = 16.sp
            )
        }
    }
}