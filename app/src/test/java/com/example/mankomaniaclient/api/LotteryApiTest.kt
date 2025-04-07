package com.example.mankomaniaclient.api

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LotteryApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var lotteryApi: LotteryApi

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        lotteryApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LotteryApi::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getCurrentAmount returns correct value`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("5000"))

        val result = lotteryApi.getCurrentAmount()

        assertTrue(result.isSuccessful)
        assertEquals(5000, result.body())
    }

    @Test
    fun `payToLottery returns correct response`() = runTest {
        val json = """{"success":true,"newAmount":15000,"message":"Payment successful"}"""
        mockWebServer.enqueue(MockResponse().setBody(json))

        val result = lotteryApi.payToLottery("player1", 5000, "test")

        assertTrue(result.isSuccessful)
        assertEquals(15000, result.body()?.newAmount)
    }
}