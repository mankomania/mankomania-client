package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Log

/**
 * WebSocket-based Lottery API
 * Uses existing WebSocket connection instead of opening new HTTP connections
 */
class LotteryApi {

    private val json = Json { ignoreUnknownKeys = true }

    // StateFlows for lottery responses
    private val _currentAmount = MutableStateFlow(0)
    val currentAmount: StateFlow<Int> = _currentAmount

    private val _lastPaymentResponse = MutableStateFlow<PaymentResponse?>(null)
    val lastPaymentResponse: StateFlow<PaymentResponse?> = _lastPaymentResponse

    private val _lastClaimResponse = MutableStateFlow<ClaimResponse?>(null)
    val lastClaimResponse: StateFlow<ClaimResponse?> = _lastClaimResponse

    init {
        // Subscribe to lottery topics via existing WebSocket
        subscribeToLotteryTopics()
    }

    /**
     * Subscribe to lottery-related WebSocket topics
     */
    private fun subscribeToLotteryTopics() {
        // Subscribe to lottery amount updates
        WebSocketService.subscribeToTopic("/topic/lottery/amount") { amount ->
            _currentAmount.value = amount.toIntOrNull() ?: 0
            Log.d("LotteryApi", "Amount updated: $amount")
        }

        // Subscribe to payment responses
        WebSocketService.subscribeToTopic("/topic/lottery/payment") { response ->
            try {
                val paymentResponse = json.decodeFromString<PaymentResponse>(response)
                _lastPaymentResponse.value = paymentResponse
                Log.d("LotteryApi", "Payment response: $paymentResponse")
            } catch (e: Exception) {
                Log.e("LotteryApi", "Error parsing payment response: ${e.message}")
            }
        }

        // Subscribe to claim responses
        WebSocketService.subscribeToTopic("/topic/lottery/claim") { response ->
            try {
                val claimResponse = json.decodeFromString<ClaimResponse>(response)
                _lastClaimResponse.value = claimResponse
                Log.d("LotteryApi", "Claim response: $claimResponse")
            } catch (e: Exception) {
                Log.e("LotteryApi", "Error parsing claim response: ${e.message}")
            }
        }
    }

    /**
     * Request current lottery amount via WebSocket
     */
    suspend fun getCurrentAmount(): Int? {
        // Send request via WebSocket instead of HTTP
        WebSocketService.send("/app/lottery/current", "")
        return _currentAmount.value
    }

    /**
     * Pay to lottery via WebSocket
     */
    suspend fun payToLottery(playerId: String, amount: Int, reason: String): PaymentResponse? {
        val request = PaymentRequest(playerId, amount, reason)
        val message = json.encodeToString(PaymentRequest.serializer(), request)

        WebSocketService.send("/app/lottery/pay", message)

        // Return cached response (in real implementation, you might want to wait for response)
        return _lastPaymentResponse.value
    }

    /**
     * Claim lottery via WebSocket
     */
    suspend fun claimLottery(playerId: String): ClaimResponse? {
        val request = ClaimRequest(playerId)
        val message = json.encodeToString(ClaimRequest.serializer(), request)

        WebSocketService.send("/app/lottery/claim", message)

        // Return cached response (in real implementation, you might want to wait for response)
        return _lastClaimResponse.value
    }

    @Serializable
    data class PaymentRequest(val playerId: String, val amount: Int, val reason: String)

    @Serializable
    data class ClaimRequest(val playerId: String)

    @Serializable
    data class PaymentResponse(val success: Boolean, val newAmount: Int, val message: String)

    @Serializable
    data class ClaimResponse(val wonAmount: Int, val newAmount: Int, val message: String)
}