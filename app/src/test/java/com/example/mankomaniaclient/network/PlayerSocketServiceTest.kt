package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompReceipt
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Helper function to access private field for testing (MUST be at the top-level or in a companion object if restricted)
// Ensure this is OUTSIDE the PlayerSocketServiceTest class
private operator fun <T> Any.get(propertyName: String): T? {
    return try {
        val property = this::class.java.getDeclaredField(propertyName)
        property.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        property.get(this) as? T
    } catch (e: NoSuchFieldException) {
        null // Or throw an error if the field is expected to always exist
    }
}

@ExperimentalCoroutinesApi
class PlayerSocketServiceTest {

    private lateinit var stompClient: StompClient
    private lateinit var stompSession: StompSession
    private lateinit var playerSocketService: PlayerSocketService
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private lateinit var messageFlow: MutableSharedFlow<StompFrame.Message>


    @BeforeEach
    fun setUp() {
        stompClient = mockk()
        stompSession = mockk(relaxUnitFun = true)

        messageFlow = MutableSharedFlow()

        coEvery { stompClient.connect(any()) } returns stompSession
        coEvery { stompSession.subscribe(any<StompSubscribeHeaders>()) } returns messageFlow

        playerSocketService = PlayerSocketService(stompClient, testScope)
    }

    @AfterEach
    fun tearDown() = testScope.runTest {
        playerSocketService.disconnect()
    }

    @Test
    fun `connect establishes session and subscribes to money updates`() = testScope.runTest {
        val url = "ws://se2-demo.aau.at:53210/ws"
        val topic = "/topic/testMoney"
        val headersSlot = slot<StompSubscribeHeaders>()

        playerSocketService.connect(url, topic)
        testScheduler.advanceUntilIdle()

        coVerify { stompClient.connect(url) }
        coVerify { stompSession.subscribe(capture(headersSlot)) }
        assertEquals(topic, headersSlot.captured.destination)
        assertNotNull(playerSocketService.playerStateFlow)

        val initialState = playerSocketService.playerStateFlow.value
        val financialStateUpdate = PlayerFinancialState(1, 2, 3, 4)
        val jsonUpdate = Json.encodeToString(PlayerFinancialState.serializer(), financialStateUpdate)

        val mockFrame = mockk<StompFrame.Message>()
        every { mockFrame.bodyAsText } returns jsonUpdate

        messageFlow.emit(mockFrame)
        testScheduler.advanceUntilIdle()

        assertEquals(financialStateUpdate, playerSocketService.playerStateFlow.value)
        if (financialStateUpdate != PlayerFinancialState(0,0,0,0)) {
            assertEquals(false, initialState == playerSocketService.playerStateFlow.value)
        }
    }

    @Test
    fun `sendMoneyUpdate sends correct JSON to the server`() = testScope.runTest {
        playerSocketService.connect()
        testScheduler.advanceUntilIdle()

        val playerId = "player123"
        val expectedUpdate = PlayerMoneyUpdate(playerId)
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate.serializer(), expectedUpdate)
        val jsonSlot = slot<String>()

        coEvery { stompSession.sendText(eq("/app/updateMoney"), capture(jsonSlot)) } returns null

        playerSocketService.sendMoneyUpdate(playerId)
        testScheduler.advanceUntilIdle()

        coVerify { stompSession.sendText("/app/updateMoney", expectedJson) }
        assertEquals(expectedJson, jsonSlot.captured)
    }


    @Test
    fun `connectAndSubscribe connects and then sends money update`() = testScope.runTest {
        coEvery { stompSession.sendText(any(), any()) } returns null
        val headersSlot = slot<StompSubscribeHeaders>()

        val playerId = "playerConnectSub"

        playerSocketService.connectAndSubscribe(playerId)
        testScheduler.advanceUntilIdle()

        coVerify { stompClient.connect(any()) }
        coVerify { stompSession.subscribe(capture(headersSlot)) }
        assertEquals("/topic/playerMoney", headersSlot.captured.destination)
        coVerify { stompSession.sendText(eq("/app/updateMoney"), any()) }
    }

    @Test
    fun `disconnect closes the session`() = testScope.runTest { // Line 141 in your screenshot
        playerSocketService.connect()
        testScheduler.advanceUntilIdle()

        // This line uses the helper. Ensure playerSocketService is initialized.
        assertNotNull(playerSocketService["session"]) // Line 146 in your screenshot

        playerSocketService.disconnect() // Line 148 in your screenshot
        testScheduler.advanceUntilIdle()

        coVerify { stompSession.disconnect() }
        // Optionally, assert that the session is now null using the helper again
        // assertNull(playerSocketService["session"]) // If you want to be explicit
    }

    @Test
    fun `playerStateFlow emits initial default state`() {
        val expectedInitialState = PlayerFinancialState(0, 0, 0, 0)
        assertEquals(expectedInitialState, playerSocketService.playerStateFlow.value)
    }
}
