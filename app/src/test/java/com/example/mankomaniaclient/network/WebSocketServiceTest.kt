package com.example.mankomaniaclient.network

import android.util.Log
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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


// src/test/java/…/WebSocketServiceTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketServiceTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope      = CoroutineScope(dispatcher)

    private val stompClient: StompClient  = mockk()
    private val session    : StompSession = mockk(relaxed = true)

    private lateinit var service: WebSocketService

    @BeforeEach fun setUp() {
        mockkStatic(Log::class)                  // ① Log.* mocken
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        service = WebSocketService(stompClient, scope)
    }

    @AfterEach
    fun tearDown() = unmockkAll()

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
        // Arrange
        coEvery { stompClient.connect(any()) } throws RuntimeException("boom")

        // Act
        service.connect("urlX", "/topic/X")
        advanceUntilIdle()

        // Assert
        coVerify { Log.e("WebSocket", match { it.contains("boom") }) }
    }

    @Test
    fun connect_collectsAndLogsIncomingMessages() = runTest(dispatcher.scheduler) {
        // Arrange
        mockkStatic(StompSession::subscribeText)
        val messages = listOf("first", "second")
        coEvery { session.subscribeText(any()) } returns messages.asFlow()
        coEvery { stompClient.connect(any()) } returns session

        // Act
        service.connect()
        advanceUntilIdle()

        // Assert
        coVerifySequence {
            Log.d("WebSocket", "Verbindung hergestellt")
            Log.d("WebSocket", "Nachricht empfangen: first")
            Log.d("WebSocket", "Nachricht empfangen: second")
        }
    }

    @Test
    fun connect_usesCustomUrlAndTopicParameters() = runTest(dispatcher.scheduler) {
        // Arrange
        val customUrl = "ws://host:1234/ws"
        val customTopic = "/topic/custom"
        coEvery { stompClient.connect(customUrl) } returns session
        mockkStatic(StompSession::subscribeText)
        coEvery { session.subscribeText(customTopic) } returns emptyFlow()

        // Act
        service.connect(customUrl, customTopic)
        advanceUntilIdle()

        // Assert
        coVerify { stompClient.connect(customUrl) }
        coVerify { session.subscribeText(customTopic) }
    }





}
