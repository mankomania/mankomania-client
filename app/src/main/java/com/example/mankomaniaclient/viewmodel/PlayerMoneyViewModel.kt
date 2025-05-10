package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.mankomaniaclient.ui.model.PlayerFinancialState

class PlayerMoneyViewModel : ViewModel() {

    private val _financialState = MutableStateFlow(
        PlayerFinancialState(10, 5, 4, 7) // default test data
    )
    val financialState: StateFlow<PlayerFinancialState> = _financialState

    fun updateFromServer(newState: PlayerFinancialState) {
        _financialState.value = newState
    }
}