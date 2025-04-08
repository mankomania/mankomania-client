package com.example.mankomaniaclient.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object LotteryTestTags {
    const val TITLE = "lotteryTitle"
    const val AMOUNT = "amountText"
    const val PAY_5K = "pay5kButton"
    const val PAY_10K = "pay10kButton"
    const val CLAIM = "claimButton"
    const val NOTIFICATION = "notificationText"
    const val LOADING = "loadingIndicator"
}

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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag(LotteryTestTags.TITLE)
        )

        Text(
            text = "$currentAmount €",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag(LotteryTestTags.AMOUNT)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onPayClick(5000) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag(LotteryTestTags.PAY_5K)
        ) {
            Text("Pay 5.000 €")
        }

        Button(
            onClick = { onPayClick(10000) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag(LotteryTestTags.PAY_10K)
        ) {
            Text("Pay 10.000 €")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClaimClick,
            enabled = !isLoading && currentAmount > 0,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag(LotteryTestTags.CLAIM)
        ) {
            Text("CLAIM LOTTERY")
        }

        if (notification.isNotEmpty()) {
            Text(
                text = notification,
                modifier = Modifier.testTag(LotteryTestTags.NOTIFICATION)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.testTag(LotteryTestTags.LOADING)
            )
        }
    }
}