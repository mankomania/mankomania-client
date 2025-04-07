package com.example.mankomaniaclient.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryContent(
    currentAmount: Int,
    notification: String,
    isLoading: Boolean,
    paymentAnimation: Boolean,
    onPayClick: (Int) -> Unit,
    onClaimClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (paymentAnimation) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LOTTERY-POOL",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$currentAmount €",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scaleAnim)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onPayClick(5000) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Pay 5.000 €")
        }

        Button(
            onClick = { onPayClick(10000) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Pay 10.000 €")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClaimClick,
            enabled = !isLoading && currentAmount > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("CLAIM LOTTERY")
        }

        if (notification.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = notification,
                color = if (currentAmount > 0) MaterialTheme.colorScheme.primary else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}