package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.network.WebSocketService
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class HorseRaceApiTest {

    private lateinit var horseRaceApi: HorseRaceApi
    private lateinit var mockWebSocketService: WebSocketService
    private val clientCountFlow = MutableStateFlow(0)

    @BeforeEach
    fun setup() {
        // Using MockK instead of Mockito
        mockWebSocketService = mockk()
        every { mockWebSocketService.clientCount } returns clientCountFlow
        horseRaceApi = HorseRaceApi(mockWebSocketService)
    }

    @Test
    fun testSendHorseSelectionRequest() = runTest {
        val request = HorseSelectionRequest("player123", 5)
        val expectedJson = Json.encodeToString(request)

        // Using MockK's verify
        coEvery { mockWebSocketService.send("/topic/selectHorse", expectedJson) } just Runs

        horseRaceApi.sendHorseSelectionRequest(request)

        coVerify { mockWebSocketService.send("/topic/selectHorse", expectedJson) }
    }

    @Test
    fun testSendHorseSelectionRequestWithEmptyValues() = runTest {
        val request = HorseSelectionRequest("", 0)
        val expectedJson = Json.encodeToString(request)

        coEvery { mockWebSocketService.send("/topic/selectHorse", expectedJson) } just Runs

        horseRaceApi.sendHorseSelectionRequest(request)

        coVerify { mockWebSocketService.send("/topic/selectHorse", expectedJson) }
    }

    @Test
    fun testSendHorseSelectionRequestWithSpecialCharacters() = runTest {
        val request = HorseSelectionRequest("player@#\$123", 5)
        val expectedJson = Json.encodeToString(request)

        coEvery { mockWebSocketService.send("/topic/selectHorse", expectedJson) } just Runs

        horseRaceApi.sendHorseSelectionRequest(request)

        coVerify { mockWebSocketService.send("/topic/selectHorse", expectedJson) }
    }

    @Test
    fun testConnectWebSocket() {
        every {
            mockWebSocketService.connect(
                url = "ws://se2-demo.aau.at:53210/ws",
                greetingsTopic = "/topic/greetings",
                clientCountTopic = "/topic/horses"
            )
        } just Runs

        horseRaceApi.connectWebSocket()

        verify {
            mockWebSocketService.connect(
                url = "ws://se2-demo.aau.at:53210/ws",
                greetingsTopic = "/topic/greetings",
                clientCountTopic = "/topic/horses"
            )
        }
    }

    @Test
    fun testParseHorseData() {
        val json = """[
            {"id":1,"name":"Thunder","color":"black"},
            {"id":2,"name":"Lightning","color":"white"}
        ]"""
        val horses = horseRaceApi.parseHorseData(json)
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
        val horses = horseRaceApi.parseHorseData("[]")
        assertTrue(horses.isEmpty())

    }

    @Test
    fun testParseInvalidHorseData() {
        val invalidJson = "NOT JSON"
        assertFailsWith<Exception> {
            horseRaceApi.parseHorseData(invalidJson)
        }
    }

    @Test
    fun testParseIncompleteHorseData() {
        val incompleteJson = """[
            {"id":1,"name":"Thunder"},
            {"id":2}
        ]"""
        val horses = horseRaceApi.parseHorseData(incompleteJson)
        assertEquals(2, horses.size)
        assertEquals(1, horses[0].id)
        assertEquals("Thunder", horses[0].name)
        assertNull(horses[0].color)
        assertEquals(2, horses[1].id)
        assertNull(horses[1].name)
    }
}