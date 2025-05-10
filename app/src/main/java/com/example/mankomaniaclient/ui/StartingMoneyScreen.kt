package com.example.mankomaniaclient.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val animatedTotal by animateIntAsState(
        targetValue = state.bills5000 * 5_000 +
                state.bills10000 * 10_000 +
                state.bills50000 * 50_000 +
                state.bills100000 * 100_000,
        label = "TotalAmount"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Total: €${"%,d".format(animatedTotal)}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF388E3C),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DenominationBox(label: String, count: Int, color: Color) {
    val animatedCount by animateIntAsState(targetValue = count, label = "billCount")

    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "×$animatedCount",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}