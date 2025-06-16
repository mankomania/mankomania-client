package com.example.mankomaniaclient.network
/*
import android.util.Log
import com.example.mankomaniaclient.model.MoveResult
import com.example.mankomaniaclient.network.WebSocketService
import com.example.mankomaniaclient.model.PlayerStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.StompReceipt
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlinx.coroutines.flow.asFlow
import com.example.mankomaniaclient.network.WebSocketService
import com.example.mankomaniaclient.viewmodel.GameViewModel


// src/test/java/…/WebSocketServiceTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketServiceTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope      = CoroutineScope(dispatcher)

    private val stompClient: StompClient  = mockk()
    private val session    : StompSession = mockk(relaxed = true)

    private lateinit var service: WebSocketService

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        service = WebSocketService(stompClient, scope, dispatcher)   // <-- 3 args
    }

    @AfterEach fun tearDown() = unmockkAll()

    /* ------------------------------------------------------------------ */

    @Test
    fun connect_startsSubscription() = runTest(dispatcher.scheduler) {
        mockkStatic(StompSession::subscribeText)
        coEvery { session.subscribeText(any()) } returns emptyFlow()
        coEvery { stompClient.connect(any()) } returns session

        service.connect()
        advanceUntilIdle()

        coVerify { stompClient.connect("ws://se2-demo.aau.at:53210/ws") }
        confirmVerified(stompClient)
    }

    @Test
    fun send_forwardsToSession() = runTest(dispatcher.scheduler) {
        val receipt: StompReceipt = mockk()
        mockkStatic(StompSession::sendText)
        coEvery { session.sendText(any(), any()) } returns receipt

        WebSocketService::class.java.getDeclaredField("session").apply {
            isAccessible = true
            set(service, session)
        }

        service.send("/app/greetings", "hello local")
        advanceUntilIdle()

        coVerify { session.sendText("/app/greetings", "hello local") }
    }

    @Test
    fun connect_logsErrorOnConnectException() = runTest(dispatcher.scheduler) {
        coEvery { stompClient.connect(any()) } throws RuntimeException("boom")

        service.connect("urlX", "/topic/X")
        advanceUntilIdle()

        coVerify { Log.e("WebSocket", match { it.contains("boom") }) }
    }

    @Test
    fun connect_collectsAndLogsIncomingMessages() = runTest(dispatcher.scheduler) {
        mockkStatic(StompSession::subscribeText)
        val messages = listOf("first", "second")

        // 1) allgemeiner Stub zuerst
        coEvery { session.subscribeText(any()) } returns emptyFlow()
        // 2) spezieller Stub zuletzt – überschreibt nur das greetings-Topic
        coEvery { session.subscribeText("/topic/greetings") } returns messages.asFlow()

        coEvery { stompClient.connect(any()) } returns session

        service.connect()
        advanceUntilIdle()

        coVerifySequence {
            Log.d("WebSocket", "Connection established")
            Log.d("WebSocket", "Greeting received: first")
            Log.d("WebSocket", "Greeting received: second")
        }
    }



    @Test
    fun connect_usesCustomUrlAndTopicParameters() = runTest(dispatcher.scheduler) {
        val customUrl   = "ws://host:1234/ws"
        val customTopic = "/topic/custom"
        coEvery { stompClient.connect(customUrl) } returns session
        mockkStatic(StompSession::subscribeText)
        coEvery { session.subscribeText(customTopic) } returns emptyFlow()

        service.connect(customUrl, customTopic)
        advanceUntilIdle()

        coVerify { stompClient.connect(customUrl) }
        coVerify { session.subscribeText(customTopic) }
    }

    @Test
    fun connect_returnsEarlyWhenAlreadyConnected() = runTest(dispatcher.scheduler) {
        // first connect succeeds
        coEvery { stompClient.connect(any()) } returns session
        service.connect()
        advanceUntilIdle()

        // second connect() must NOT hit the client again
        service.connect()
        advanceUntilIdle()

        coVerify(exactly = 1) { stompClient.connect(any()) }
    }

    @Test
    fun send_logsErrorWhenSessionThrows() = runTest(dispatcher.scheduler) {
        mockkStatic(StompSession::sendText)
        coEvery { session.sendText(any(), any()) } throws RuntimeException("fail")

        WebSocketService::class.java.getDeclaredField("session").apply {
            isAccessible = true
            set(service, session)
        }

        service.send("/app/foo", "bar")
        advanceUntilIdle()

        coVerify { Log.e("WebSocket", match { it.contains("Send error") }) }
    }

    @Test
    fun disconnect_closesSessionAndResetsState() = runTest(dispatcher.scheduler) {
        WebSocketService::class.java.getDeclaredField("session").apply {
            isAccessible = true
            set(service, session)
        }
        service.connect()            // mark as connected
        advanceUntilIdle()

        service.disconnect()
        advanceUntilIdle()

        coVerify { session.disconnect() }
        assertEquals(0, service.clientCount.value)
    }

@Test
fun `subscribe to player-moved forwards MoveResult to ViewModel`() = runTest(dispatcher.scheduler) {
    val jsonMove = """
        {
            "newPosition": 4,
            "oldPosition": 2,
            "fieldType": "CasinoAction",
            "fieldDescription": "Try your luck!",
            "playersOnField": ["Toni", "Jorge"]
        }
    """.trimIndent()

    val fakeFlow = listOf(jsonMove).asFlow()
    val mockViewModel = mockk<GameViewModel>(relaxed = true)

    WebSocketService::class.java.getDeclaredField("gameViewModel").apply {
        isAccessible = true
        set(WebSocketService, mockViewModel)
    }

    coEvery { session.subscribeText("/topic/player-moved") } returns fakeFlow
    coEvery { stompClient.connect(any()) } returns session

    WebSocketService.connect()
    advanceUntilIdle()

    coVerify {
        mockViewModel.onPlayerMoved(
            MoveResult(
                newPosition = 4,
                oldPosition = 2,
                fieldType = "CasinoAction",
                fieldDescription = "Try your luck!",
                playersOnField = listOf("Toni", "Jorge")
            )
        )
    }

    @Test
fun `subscribeToPlayerStatuses forwards PlayerStatus to GameViewModel`() = runTest(dispatcher.scheduler) {
    val jsonStatus = """
        {
            "name": "Toni",
            "position": 4,
            "balance": 120000,
            "money": {
                "5000": 3,
                "10000": 2
            },
            "isTurn": true

        }
    """.trimIndent()

    val fakeFlow = listOf(jsonStatus).asFlow()
    val mockViewModel = mockk<GameViewModel>(relaxed = true)

    WebSocketService::class.java.getDeclaredField("gameViewModel").apply {
        isAccessible = true
        set(WebSocketService, mockViewModel)
    }

    WebSocketService::class.java.getDeclaredField("session").apply {
        isAccessible = true
        set(WebSocketService, session)
    }

    coEvery { session.subscribeText("/topic/player/Toni/status") } returns fakeFlow

    WebSocketService.subscribeToPlayerStatuses(listOf("Toni"))
    advanceUntilIdle()

    coVerify {
        mockViewModel.updatePlayerStatus(
            PlayerStatus(
                name = "Toni",
                position = 4,
                balance = 120000,
                money = mapOf(5000 to 3, 10000 to 2),
                isTurn = true


            )
        )
    }
}
}
}
*/

