package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

// Wrapper class to avoid implementing StompSession directly
class MockSessionWrapper {
    val sentMessages = mutableListOf<Pair<String, String>>()
    var disconnected = false

    fun sendText(destination: String, body: String) {
        sentMessages.add(destination to body)
    }

    fun disconnect() {
        disconnected = true
    }

    fun subscribeText(destination: String): Nothing {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    // Create a StompSession-compatible object using Any and casting
    fun asStompSession(): StompSession {
        return this as StompSession
    }
}

// Wrapper class to avoid implementing StompClient directly
class MockClientWrapper {
    private val mockSession = MockSessionWrapper()

    fun connect(url: String, login: String? = null, passcode: String? = null): StompSession {
        return mockSession.asStompSession()
    }

    fun connect(
        url: String,
        login: String? = null,
        passcode: String? = null,
        customStompConnectHeaders: Map<String, List<String>>? = null,
        additionalWebSocketHeaders: Map<String, List<String>>? = null
    ): StompSession {
        return mockSession.asStompSession()
    }

    fun connect(url: String, config: () -> Unit): StompSession {
        return mockSession.asStompSession()
    }

    fun newWebSocketClient(): Nothing {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    // Create a StompClient-compatible object using Any and casting
    fun asStompClient(): StompClient {
        return this as StompClient
    }
}

@ExperimentalCoroutinesApi
class PlayerSocketServiceTest {

    private lateinit var playerSocketService: PlayerSocketService
    private lateinit var mockClient: MockClientWrapper
    private val testDispatcher = StandardTestDispatcher()
    private val testCoroutineScope = CoroutineScope(testDispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockClient = MockClientWrapper()
        playerSocketService = PlayerSocketService(mockClient.asStompClient(), testCoroutineScope)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial player state flow should have zero values`() = runTest {
        val initialState = playerSocketService.playerStateFlow.value
        assertEquals(
            PlayerFinancialState(bills5000 = 0, bills10000 = 0, bills50000 = 0, bills100000 = 0),
            initialState
        )
    }

    @Test
    fun `sendMoneyUpdate should send message to correct destination`() = runTest {
        playerSocketService.connect() // Initialize session
        val playerId = "testPlayer"
        playerSocketService.sendMoneyUpdate(playerId)

        assertEquals(1, mockClient.connect("").sentMessages.size)
        assertEquals("/app/updateMoney", mockClient.connect("").sentMessages[0].first)
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate.serializer(), PlayerMoneyUpdate(playerId))
        assertEquals(expectedJson, mockClient.connect("").sentMessages[0].second)
    }

    @Test
    fun `disconnect should clear session and job`() = runTest {
        playerSocketService.connect() // Initialize session
        playerSocketService.disconnect()

        val sessionField = PlayerSocketService::class.java.getDeclaredField("session")
        sessionField.isAccessible = true
        assertNull(sessionField.get(playerSocketService))

        val jobField = PlayerSocketService::class.java.getDeclaredField("moneySubscriptionJob")
        jobField.isAccessible = true
        assertNull(jobField.get(playerSocketService))

        assertTrue(mockClient.connect("").disconnected)
    }

    @Test
    fun `connectAndSubscribe should call connect and sendMoneyUpdate`() = runTest {
        val playerId = "testPlayer"
        playerSocketService.connectAndSubscribe(playerId)

        assertEquals(1, mockClient.connect("").sentMessages.size)
        assertEquals("/app/updateMoney", mockClient.connect("").sentMessages[0].first)
    }
}