package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.LotteryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class LotteryViewModel(
    private val api: LotteryApi
) : ViewModel() {
    private val _currentAmount = MutableStateFlow(0)
    val currentAmount: StateFlow<Int> = _currentAmount
    private val _notification = MutableStateFlow("")
    val notification: StateFlow<String> = _notification
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _paymentAnimation = MutableStateFlow(false)
    val paymentAnimation: StateFlow<Boolean> = _paymentAnimation
    init {
        viewModelScope.launch {
            refreshAmount()
        }
    }
    fun refreshAmount() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.getCurrentAmount()
                if (response.isSuccessful) {
                    _currentAmount.value = response.body() ?: 0
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
                val response = api.payToLottery(playerId, amount, reason)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            _currentAmount.value = it.newAmount
                            _notification.value = "$reason - $amount € added to lottery"
                            _paymentAnimation.value = true
                        }
                    }
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
                val response = api.claimLottery(playerId)
                if (response.isSuccessful) {
                    response.body()?.let { claimResponse ->
                        _currentAmount.value = claimResponse.newAmount
                        _notification.value = if (claimResponse.wonAmount > 0) {
                            "You won ${claimResponse.wonAmount} €!"
                        } else {
                            "Lottery was empty! 50,000 € added to pool"
                        }
                    }
                }
            } catch (e: Exception) {
                _notification.value = "Claim failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}