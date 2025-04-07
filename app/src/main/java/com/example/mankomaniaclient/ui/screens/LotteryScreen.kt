package com.example.mankomaniaclient.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mankomaniaclient.ui.components.LotteryContent
import com.example.mankomaniaclient.viewmodel.LotteryViewModel
@Composable
fun LotteryScreen(
    playerId: String,
    viewModel: LotteryViewModel = viewModel()
) {
    val currentAmount by viewModel.currentAmount.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val paymentAnimation by viewModel.paymentAnimation.collectAsState()
    LotteryContent(
        currentAmount = currentAmount,
        notification = notification,
        isLoading = isLoading,
        paymentAnimation = paymentAnimation,
        onPayClick = { amount ->
            viewModel.payToLottery(playerId, amount, "Lottery Payment")
        },
        onClaimClick = { viewModel.claimLottery(playerId) }
    )
}