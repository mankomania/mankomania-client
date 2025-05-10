package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerSocketServiceTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private lateinit var stompClient: StompClient
    private lateinit var stompSession: StompSession
    private lateinit var socketService: PlayerSocketService
    private lateinit var messageFlow: MutableSharedFlow<StompFrame.Message>

    @BeforeEach
    fun setup() {
        stompClient = mockk()
        stompSession = mockk(relaxUnitFun = true)
        messageFlow = MutableSharedFlow()

        coEvery { stompClient.connect(any()) } returns stompSession
        coEvery { stompSession.subscribe(any<StompSubscribeHeaders>()) } returns messageFlow
        coEvery { stompSession.send(any<StompSendHeaders>(), any<FrameBody>()) } returns mockk()

        socketService = PlayerSocketService(stompClient, testScope)
    }

    @AfterEach
    fun tearDown() {
        runBlocking(testDispatcher) {
            socketService.disconnect()
        }
        testScheduler.advanceUntilIdle()
    }

    @Test
    fun `playerStateFlow emits initial default state`() = testScope.runTest {
        val expectedInitialState = PlayerFinancialState(0, 0, 0, 0)
        assertEquals(expectedInitialState, socketService.playerStateFlow.value)
    }

    @Test
    fun `connect establishes session and subscribes to money updates`() = testScope.runTest {
        val url = "ws://se2-demo.aau.at:53210/ws"
        val topic = "/topic/testMoney"
        val headersSlot = slot<StompSubscribeHeaders>()

        coEvery { stompSession.subscribe(capture(headersSlot)) } returns messageFlow

        socketService.connect(url, topic)
        testScheduler.advanceUntilIdle()

        coVerify { stompClient.connect(url) }
        coVerify { stompSession.subscribe(headersSlot.captured) }
        assertEquals(topic, headersSlot.captured.destination)

        val update = PlayerFinancialState(1, 2, 3, 4)
        val updateJson = Json.encodeToString(PlayerFinancialState.serializer(), update)
        val mockMessage = mockk<StompFrame.Message> {
            every { bodyAsText } returns updateJson
        }

        messageFlow.emit(mockMessage)
        testScheduler.advanceUntilIdle()

        assertEquals(update, socketService.playerStateFlow.value)
    }

    @Test
    fun `sendMoneyUpdate sends JSON with correct headers and body`() = testScope.runTest {
        val playerId = "valentina"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate.serializer(), PlayerMoneyUpdate(playerId))

        val headerSlot = slot<StompSendHeaders>()
        val bodySlot = slot<FrameBody>()

        coEvery {
            stompSession.send(capture(headerSlot), capture(bodySlot))
        } returns mockk()

        socketService.connect()
        socketService.sendMoneyUpdate(playerId)
        testScheduler.advanceUntilIdle()

        coVerify { stompSession.send(any(), any()) }

        assertEquals("/app/updateMoney", headerSlot.captured.destination)
        assertNotNull(bodySlot.captured)
        assertEquals(expectedJson, (bodySlot.captured as FrameBody.Text).text)
    }

    @Test
    fun `connectAndSubscribe connects and sends update`() = testScope.runTest {
        val playerId = "test-player"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate.serializer(), PlayerMoneyUpdate(playerId))

        val subscribeHeadersSlot = slot<StompSubscribeHeaders>()
        val sendHeadersSlot = slot<StompSendHeaders>()
        val frameBodySlot = slot<FrameBody>()

        coEvery { stompSession.subscribe(capture(subscribeHeadersSlot)) } returns messageFlow
        coEvery { stompSession.send(capture(sendHeadersSlot), capture(frameBodySlot)) } returns mockk()

        socketService.connectAndSubscribe(playerId)
        testScheduler.advanceUntilIdle()

        coVerify { stompClient.connect(any()) }
        coVerify { stompSession.subscribe(subscribeHeadersSlot.captured) }
        assertEquals("/topic/playerMoney", subscribeHeadersSlot.captured.destination)

        coVerify { stompSession.send(sendHeadersSlot.captured, frameBodySlot.captured) }
        assertEquals("/app/updateMoney", sendHeadersSlot.captured.destination)
        assertEquals(expectedJson, (frameBodySlot.captured as FrameBody.Text).text)
    }

    @Test
    fun `disconnect closes the session`() = testScope.runTest {
        socketService.connect()
        testScheduler.advanceUntilIdle()

        socketService.disconnect()
        testScheduler.advanceUntilIdle()

        coVerify { stompSession.disconnect() }
    }
}