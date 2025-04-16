package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.LotteryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LotteryViewModel : ViewModel() {

    private val _currentAmount = MutableStateFlow(0)
    val currentAmount: StateFlow<Int> = _currentAmount

    private val _notification = MutableStateFlow("")
    val notification: StateFlow<String> = _notification

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _paymentAnimation = MutableStateFlow(false)
    val paymentAnimation: StateFlow<Boolean> = _paymentAnimation

    init {
        refreshAmount()
    }

    fun refreshAmount() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val amount = LotteryApi.getCurrentAmount()
                if (amount != null) {
                    _currentAmount.value = amount
                } else {
                    _notification.value = "Couldn't load lottery amount"
                }
            } catch (e: Exception) {
                _notification.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun payToLottery(playerId: String, amount: Int, reason: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = LotteryApi.payToLottery(playerId, amount, reason)
                if (result != null && result.success) {
                    _currentAmount.value = result.newAmount
                    _notification.value = "$reason - $amount € added to lottery"
                    _paymentAnimation.value = true
                } else {
                    _notification.value = result?.message ?: "Payment failed"
                }
            } catch (e: Exception) {
                _notification.value = "Payment failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun claimLottery(playerId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = LotteryApi.claimLottery(playerId)
                if (result != null) {
                    _currentAmount.value = result.newAmount
                    _notification.value = if (result.wonAmount > 0) {
                        "You won ${result.wonAmount} €!"
                    } else {
                        "Lottery was empty! 50,000 € added to pool"
                    }
                } else {
                    _notification.value = "Claim failed"
                }
            } catch (e: Exception) {
                _notification.value = "Claim failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
