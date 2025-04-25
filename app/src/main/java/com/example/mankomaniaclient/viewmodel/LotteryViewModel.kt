package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.LotteryApi
import kotlinx.coroutines.launch

class LotteryViewModel(
    private val lotteryApi: LotteryApi = LotteryApi()
) : ViewModel() {

    private val _currentAmount = MutableLiveData(0)
    val currentAmount: LiveData<Int> = _currentAmount

    private val _notification = MutableLiveData<String>()
    val notification: LiveData<String> = _notification

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _paymentAnimation = MutableLiveData(false)
    val paymentAnimation: LiveData<Boolean> = _paymentAnimation

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