package com.example.mankomaniaclient.api

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LotteryApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var lotteryApi: LotteryApi

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

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
    fun `get current amount success`() {
        mockWebServer.enqueue(MockResponse().setBody("5000"))

        val response = runBlocking { lotteryApi.getCurrentAmount() }

        assert(response.isSuccessful)
        assertEquals(5000, response.body())
    }

    @Test
    fun `pay to lottery success`() {
        val json = """{"success":true,"newAmount":15000,"message":"Payment successful"}"""
        mockWebServer.enqueue(MockResponse().setBody(json))

        val response = runBlocking {
            lotteryApi.payToLottery("player1", 5000, "test")
        }

        assert(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(15000, response.body()?.newAmount)
    }

    @Test
    fun `claim lottery success`() {
        val json = """{"wonAmount":20000,"newAmount":0,"message":"You won!"}"""
        mockWebServer.enqueue(MockResponse().setBody(json))

        val response = runBlocking { lotteryApi.claimLottery("player1") }

        assert(response.isSuccessful)
        assertEquals(20000, response.body()?.wonAmount)
    }
}