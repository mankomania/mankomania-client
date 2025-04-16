package com.example.mankomaniaclient.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object LotteryApi {

    private const val BASE_URL = "http://se2-demo.aau.at:53210"

    suspend fun getCurrentAmount(): Int? = withContext(Dispatchers.IO) {
        try {
            val connection = URL("$BASE_URL/lottery/current").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == 200) {
                connection.inputStream.bufferedReader().readText().toIntOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun payToLottery(playerId: String, amount: Int, reason: String): PaymentResponse? = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL/lottery/pay?playerId=$playerId&amount=$amount&reason=$reason"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                PaymentResponse(
                    success = json.getBoolean("success"),
                    newAmount = json.getInt("newAmount"),
                    message = json.getString("message")
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun claimLottery(playerId: String): ClaimResponse? = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL/lottery/claim?playerId=$playerId"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                ClaimResponse(
                    wonAmount = json.getInt("wonAmount"),
                    newAmount = json.getInt("newAmount"),
                    message = json.getString("message")
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class PaymentResponse(val success: Boolean, val newAmount: Int, val message: String)
    data class ClaimResponse(val wonAmount: Int, val newAmount: Int, val message: String)
}
