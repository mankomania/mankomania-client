package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.mockito.Mockito.`when` as whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
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
        // Arrange - Create a normal horse selection request
        val request = HorseSelectionRequest("player123", 5)
        val expectedJson: String = Gson().toJson(request)

        // Act - Call the method being tested
        horseRaceApi.sendHorseSelectionRequest(request)

        // Assert - Verify that send was called with the correct parameters
        verify(mockWebSocketService).send("/topic/selectHorse", expectedJson)
    }

    @Test
    fun testSendHorseSelectionRequestWithEmptyValues() = runTest {
        // Arrange - Create request with empty player ID and zero horse ID
        val request = HorseSelectionRequest("", 0)
        val expectedJson: String = Gson().toJson(request)

        // Act - Call method being tested
        horseRaceApi.sendHorseSelectionRequest(request)

        // Assert - Verify send was called with the correct parameters
        verify(mockWebSocketService).send("/topic/selectHorse", expectedJson)
    }

    @Test
    fun testSendHorseSelectionRequestWithSpecialCharacters() = runTest {
        // Arrange - Create request with special characters in player ID
        val request = HorseSelectionRequest("player@#$123", 5)
        val expectedJson: String = Gson().toJson(request)

        // Act - Call method being tested
        horseRaceApi.sendHorseSelectionRequest(request)

        // Assert - Verify send was called with the correct parameters
        verify(mockWebSocketService).send("/topic/selectHorse", expectedJson)
    }

    @Test
    fun testConnectWebSocket() {
        // Act - Call the extracted WebSocket connection method
        horseRaceApi.connectWebSocket()

        // Assert - Verify that connect was called with the correct parameters
        verify(mockWebSocketService).connect(
            url = "ws://se2-demo.aau.at:53210/ws",
            greetingsTopic = "/topic/greetings",
            clientCountTopic = "/topic/horses"
        )
    }

    @Test
    fun testParseHorseData() {
        // Arrange - Create test JSON with complete horse data
        val json: String = """[
            {"id":1,"name":"Thunder","color":"black"},
            {"id":2,"name":"Lightning","color":"white"}
        ]"""

        // Act - Parse the JSON data
        val horses: List<Horse> = horseRaceApi.parseHorseData(json)

        // Assert - Verify that the JSON was correctly parsed into Horse objects
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Thunder", horses[0].name)
        assertEquals("black", horses[0].color)
        assertEquals(2, horses[1].id)
        assertEquals("Lightning", horses[1].name)
        assertEquals("white", horses[1].color)
    }

    @Test
    fun testParseEmptyHorseData() {
        // Arrange - Create an empty JSON array
        val emptyJson: String = "[]"

        // Act - Parse the empty JSON data
        val horses: List<Horse> = horseRaceApi.parseHorseData(emptyJson)

        // Assert - Verify that the result is an empty list
        assertTrue(horses.isEmpty())
    }

    @Test
    fun testParseInvalidHorseData() {
        // Arrange - Create invalid JSON data
        val invalidJson: String = "NOT JSON"

        // Act & Assert - Verify that parsing invalid JSON throws JsonSyntaxException
        assertFailsWith<JsonSyntaxException> {
            horseRaceApi.parseHorseData(invalidJson)
        }
    }

    @Test
    fun testParseIncompleteHorseData() {
        // Arrange - Create JSON with incomplete horse data (missing fields)
        val incompleteJson: String = """[
            {"id":1,"name":"Thunder"},
            {"id":2}
        ]"""

        // Act - Parse the incomplete JSON data
        val horses: List<Horse> = horseRaceApi.parseHorseData(incompleteJson)

        // Assert - Verify correct parsing of available fields and null for missing fields
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Thunder", horses[0].name)
        assertNull(horses[0].color)
        assertEquals(2, horses[1].id)
        assertNull(horses[1].name)
    }
}