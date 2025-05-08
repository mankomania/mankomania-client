package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.Mockito.`when` as whenever
import kotlin.test.assertEquals

class HorseRaceApiTest {

    private lateinit var horseRaceApi: HorseRaceApi
    private lateinit var mockWebSocketService: WebSocketService
    private val clientCountFlow = MutableStateFlow<Int>(0)

    @BeforeEach
    fun setup() {
        // Create mock WebSocketService
        mockWebSocketService = mock(WebSocketService::class.java)
        whenever(mockWebSocketService.clientCount).thenReturn(clientCountFlow)

        // Initialize HorseRaceApi with mock
        horseRaceApi = HorseRaceApi(mockWebSocketService)
    }

    @Test
    fun testSendHorseSelectionRequest() = runTest {
        // Create test data
        val request = HorseSelectionRequest(playerId = "1", horseId = 2)

        // Call method being tested
        horseRaceApi.sendHorseSelectionRequest(request)

        // Verify WebSocketService.send was called with correct parameters
        verify(mockWebSocketService).send("/topic/selectHorse", Gson().toJson(request))
    }

    @Test
    fun testParseHorseData() {
        // Create sample JSON
        val json = """[
            {"id":1,"name":"Thunder","color":"black"},
            {"id":2,"name":"Lightning","color":"white"}
        ]"""

        // Call method being tested
        val result = horseRaceApi.parseHorseData(json)

        // Verify results
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Thunder", result[0].name)
        assertEquals("black", result[0].color)
        assertEquals(2, result[1].id)
        assertEquals("Lightning", result[1].name)
        assertEquals("white", result[1].color)
    }

    @Test
    fun testSendMultipleHorseSelections() = runTest {
        // Create test data for multiple selections
        val request1 = HorseSelectionRequest(playerId = "1", horseId = 2)
        val request2 = HorseSelectionRequest(playerId = "1", horseId = 3)

        // Call method being tested multiple times
        horseRaceApi.sendHorseSelectionRequest(request1)
        horseRaceApi.sendHorseSelectionRequest(request2)

        // Verify WebSocketService.send was called with the first parameters
        verify(mockWebSocketService).send("/topic/selectHorse", Gson().toJson(request1))
        // Verify WebSocketService.send was called with the second parameters
        verify(mockWebSocketService).send("/topic/selectHorse", Gson().toJson(request2))
        // Verify WebSocketService.send was called exactly twice
        verify(mockWebSocketService, times(2)).send(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())
    }
}