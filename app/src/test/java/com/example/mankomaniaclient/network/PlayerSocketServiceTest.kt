package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.StompClient
import org.junit.Test
import kotlin.test.assertEquals

class PlayerSocketServiceTest {

    private val mockStompClient = mockk<StompClient>()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun `initial money state is zero`() = runTest {
        val service = PlayerSocketService(mockStompClient, testScope)

        val expected = PlayerFinancialState(
            bills5000 = 0,
            bills10000 = 0,
            bills50000 = 0,
            bills100000 = 0
        )

        val actual = service.playerStateFlow.first()
        assertEquals(expected, actual)
    }
}