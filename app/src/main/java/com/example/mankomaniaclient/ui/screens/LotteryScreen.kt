package com.example.mankomaniaclient.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mankomaniaclient.ui.components.LotteryContent
import com.example.mankomaniaclient.viewmodel.LotteryViewModel

@Composable
fun LotteryScreen(
    playerId: String,
    lotteryViewModel: LotteryViewModel
) {
    val currentAmount by lotteryViewModel.currentAmount.collectAsState()
    val notification by lotteryViewModel.notification.collectAsState()
    val isLoading by lotteryViewModel.isLoading.collectAsState()
    val paymentAnimation by lotteryViewModel.paymentAnimation.collectAsState()

    LotteryContent(
        currentAmount = currentAmount,
        notification = notification,
        isLoading = isLoading,
        paymentAnimation = paymentAnimation,
        onPayClick = { amount ->
            lotteryViewModel.payToLottery(playerId, amount, "Lottery Payment")
        },
        onClaimClick = { lotteryViewModel.claimLottery(playerId) }
    )
}
