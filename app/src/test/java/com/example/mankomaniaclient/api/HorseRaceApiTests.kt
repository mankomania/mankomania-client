package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class HorseRaceApiTests {

    @MockK
    private lateinit var mockWebSocketService: WebSocketService

    private lateinit var horseRaceApi: HorseRaceApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        // Mock the clientCount flow
        every { mockWebSocketService.clientCount } returns MutableStateFlow(0)
        // Default behavior for connect
        every { mockWebSocketService.connect(any(), any(), any()) } just runs

        horseRaceApi = HorseRaceApi(mockWebSocketService)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    @DisplayName("Test WebSocket connection parameters")
    fun testConnectWebSocket() {
        // Act
        horseRaceApi.connectWebSocket()

        // Assert
        verify {
            mockWebSocketService.connect(
                url = "ws://se2-demo.aau.at:53210/ws",
                greetingsTopic = "/topic/greetings",
                clientCountTopic = "/topic/horses"
            )
        }
    }
    @Test
    @DisplayName("Should connect to WebSocket and collect client count")
    fun `connectToServer should connect and collect client count`() {
        runTest(testDispatcher) {
            // Arrange
            val clientCountFlow = MutableStateFlow(5)
            every { mockWebSocketService.clientCount } returns clientCountFlow

            // Act
            val job = launch { horseRaceApi.connectToServer() }
            advanceUntilIdle() // Let the flow collect

            // Assert
            verify(exactly = 1) { mockWebSocketService.connect(any(), any(), any()) }
            job.cancel() // Stop the coroutine to avoid hanging
        }
    }
    @Test
    @DisplayName("Test sendHorseSelectionRequest sends correct WebSocket message")
    fun testSendHorseSelectionRequest() = runTest {
        // Arrange
        val request = HorseSelectionRequest(playerId = "player123", horseId = 7)
        coEvery { mockWebSocketService.send(any(), any()) } just runs

        // Act
        horseRaceApi.sendHorseSelectionRequest(request)

        // Assert
        coVerify {
            mockWebSocketService.send(
                eq("/topic/selectHorse"),
                match { it.contains("\"playerId\":\"player123\"") && it.contains("\"horseId\":7") }
            )
        }
    }
    @Test
    @DisplayName("Test parsing valid horse data JSON")
    fun testParseHorseDataValid() {
        // Arrange
        val validJson = """[
            {"id":1,"name":"Midnight","color":"black"},
            {"id":2,"name":"Lightning","color":"white"}
        ]"""

        // Act
        val horses = horseRaceApi.parseHorseData(validJson)

        // Assert
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Midnight", horses[0].name)
        assertEquals("black", horses[0].color)
        assertEquals(2, horses[1].id)
        assertEquals("Lightning", horses[1].name)
        assertEquals("white", horses[1].color)
    }

    @Test
    @DisplayName("Test parsing horse data with missing fields")
    fun testParseHorseDataWithMissingFields() {
        // Arrange
        val jsonWithMissingFields = """[
            {"id":1,"name":"Midnight"},
            {"id":2,"name":"Lightning","color":null}
        ]"""

        // Act
        val horses = horseRaceApi.parseHorseData(jsonWithMissingFields)

        // Assert
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Midnight", horses[0].name)
        assertNull(horses[0].color)
        assertEquals(2, horses[1].id)
        assertEquals("Lightning", horses[1].name)
        assertNull(horses[1].color)
    }

    @Test
    @DisplayName("Test parsing horse data with extra fields")
    fun testParseHorseDataWithExtraFields() {
        // Arrange
        val jsonWithExtraFields = """[
            {"id":1,"name":"Midnight","color":"black","speed":42,"owner":"John"},
            {"id":2,"name":"Lightning","color":"white","weight":500}
        ]"""

        // Act
        val horses = horseRaceApi.parseHorseData(jsonWithExtraFields)

        // Assert
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Midnight", horses[0].name)
        assertEquals("black", horses[0].color)
        assertEquals(2, horses[1].id)
        assertEquals("Lightning", horses[1].name)
        assertEquals("white", horses[1].color)
    }

    @Test
    @DisplayName("Test parsing invalid JSON throws exception")
    fun testParseHorseDataInvalid() {
        // Arrange
        val invalidJson = """[{"id":"not-a-number","name":true}]"""

        // Act & Assert
        assertThrows(Exception::class.java) {
            horseRaceApi.parseHorseData(invalidJson)
        }
    }

    @Test
    @DisplayName("Test parsing malformed JSON throws exception")
    fun testParseHorseDataMalformed() {
        // Arrange
        val malformedJson = """[{"id":1,"name":"Incomplete"""

        // Act & Assert
        assertThrows(Exception::class.java) {
            horseRaceApi.parseHorseData(malformedJson)
        }
    }

    @Test
    @DisplayName("Test parsing empty array JSON")
    fun testParseHorseDataEmptyArray() {
        // Arrange
        val emptyArrayJson = "[]"

        // Act
        val horses = horseRaceApi.parseHorseData(emptyArrayJson)

        // Assert
        assertTrue(horses.isEmpty())
    }

    @Test
    @DisplayName("Test parsing JSON with a single horse")
    fun testParseHorseDataSingleHorse() {
        // Arrange
        val singleHorseJson = """[{"id":1,"name":"Midnight","color":"black"}]"""

        // Act
        val horses = horseRaceApi.parseHorseData(singleHorseJson)

        // Assert
        assertEquals(1, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Midnight", horses[0].name)
        assertEquals("black", horses[0].color)
    }
}