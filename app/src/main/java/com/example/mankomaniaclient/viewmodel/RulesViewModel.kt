package com.example.mankomaniaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mankomaniaclient.api.GameRulesApi
import com.example.mankomaniaclient.model.GameRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RulesViewModel(
    private val apiService: GameRulesApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<RulesUiState>(RulesUiState.Loading)
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    fun fetchGameRules() {
        viewModelScope.launch {
            _uiState.value = RulesUiState.Loading
            apiService.getGameRules()
                .onSuccess { rules ->
                    _uiState.value = RulesUiState.Success(rules)
                }
                .onFailure { error ->
                    _uiState.value = RulesUiState.Error(error.message ?: "Unbekannter Fehler")
                }
        }
    }
}

sealed class RulesUiState {
    object Loading : RulesUiState()
    data class Success(val rules: GameRules) : RulesUiState()
    data class Error(val message: String) : RulesUiState()
}