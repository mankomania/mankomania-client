package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.LotteryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LotteryViewModel(
    private val lotteryApi: LotteryApi = LotteryApi()
) : ViewModel() {

    private val _currentAmount = MutableStateFlow(0)
    val currentAmount: StateFlow<Int> = _currentAmount

    private val _notification = MutableStateFlow("")
    val notification: StateFlow<String> = _notification

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _paymentAnimation = MutableStateFlow(false)
    val paymentAnimation: StateFlow<Boolean> = _paymentAnimation

    fun refreshAmount() {
        viewModelScope.launch {
            _isLoading.value = true
            val amount = lotteryApi.getCurrentAmount()
            if (amount != null) {
                _currentAmount.value = amount
            } else {
                _notification.value = "Couldn't load lottery amount"
            }
            _isLoading.value = false
        }
    }

    fun payToLottery(playerId: String, amount: Int, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = lotteryApi.payToLottery(playerId, amount, reason)
            if (response != null) {
                if (response.success) {
                    _currentAmount.value = response.newAmount
                    _notification.value = "$reason - $amount € added to lottery"
                    _paymentAnimation.value = true
                } else {
                    _notification.value = response.message
                    _paymentAnimation.value = false
                }
            } else {
                _notification.value = "Payment failed"
                _paymentAnimation.value = false
            }
            _isLoading.value = false
        }
    }

    fun claimLottery(playerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = lotteryApi.claimLottery(playerId)
            if (response != null) {
                _currentAmount.value = response.newAmount
                if (response.wonAmount > 0) {
                    _notification.value = "You won ${response.wonAmount} €!"
                } else {
                    _notification.value = "Lottery was empty! ${response.newAmount} € added to pool"
                }
            } else {
                _notification.value = "Claim failed"
            }
            _isLoading.value = false
        }
    }
}

