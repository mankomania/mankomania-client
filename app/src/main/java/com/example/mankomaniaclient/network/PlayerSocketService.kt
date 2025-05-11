package com.example.mankomaniaclient.network

import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.anyOrNull // Necessario per mockare FrameBody?
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope // Usato da runTest
import kotlinx.coroutines.test.runTest // Sostituisce TestCoroutineScope e StandardTestDispatcher
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Funzione helper per accedere a campi privati (usare con cautela)
private fun <T> Any.getPrivateField(name: String): T? {
    return try {
        val field = this::class.java.getDeclaredField(name)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        field.get(this) as? T
    } catch (e: NoSuchFieldException) {
        null
    }
}

@ExperimentalCoroutinesApi
class PlayerSocketServiceTest {

    private lateinit var stompClient: StompClient
    private lateinit var stompSession: StompSession
    private lateinit var playerSocketService: PlayerSocketService
    private lateinit var testScope: TestScope // runTest fornisce lo scope

    // Flow per simulare i messaggi in arrivo dal server
    private lateinit var serverMessages: MutableSharedFlow<StompFrame.Message>

    @BeforeEach
    fun setUp() {
        // runTest crea un TestScope, che possiamo usare se necessario,
        // ma PlayerSocketService prenderà lo scope del runTest in cui è chiamato.
        // Per coerenza, lo assegnamo qui, ma ogni @Test runTest avrà il suo.
        testScope = TestScope() // Questo è ok, ma ogni @Test userà il proprio TestScope implicito

        stompClient = mockk()
        stompSession = mockk(relaxUnitFun = true) // Per session.disconnect() e altre funzioni Unit

        serverMessages = MutableSharedFlow() // Per simulare i messaggi dal server

        // Mock di base per le chiamate STOMP sottostanti
        coEvery { stompClient.connect(any()) } returns stompSession
        coEvery { stompSession.subscribe(any<StompSubscribeHeaders>()) } returns serverMessages
        coEvery { stompSession.send(any<StompSendHeaders>(), anyOrNull<FrameBody>()) } returns null // sendText chiama questo

        // PlayerSocketService userà lo scope del blocco runTest in cui le sue funzioni suspend vengono chiamate
        playerSocketService = PlayerSocketService(stompClient, testScope) // Passiamo lo scope principale del test
    }

    @AfterEach
    fun tearDown() = runTest { // Usiamo runTest per coerenza se disconnect è suspend
        playerSocketService.disconnect()
        // TestScope gestisce la cancellazione dei job figli, incluso moneySubscriptionJob
    }

    @Test
    fun `initial playerState is default PlayerFinancialState`() = runTest {
        val expectedInitialState = PlayerFinancialState(0, 0, 0, 0)
        assertEquals(expectedInitialState, playerSocketService.playerStateFlow.value)
    }

    @Test
    fun `connect establishes session and subscribes`() = runTest {
        val testUrl = "ws://test.url"
        val testTopic = "/topic/test"
        val capturedHeaders = slot<StompSubscribeHeaders>()

        // Sovrascriviamo il mock di subscribe per catturare gli header specifici di questo test
        coEvery { stompSession.subscribe(capture(capturedHeaders)) } returns serverMessages

        playerSocketService.connect(testUrl, testTopic)

        coVerify { stompClient.connect(testUrl) }
        coVerify { stompSession.subscribe(any<StompSubscribeHeaders>()) } // Verifica che subscribe sia stato chiamato
        assertEquals(testTopic, capturedHeaders.captured.destination)
        assertNotNull(playerSocketService.getPrivateField<StompSession>("session"))
    }

    @Test
    fun `receive message updates playerStateFlow`() = runTest {
        playerSocketService.connect() // Connette con i valori di default

        val newState = PlayerFinancialState(10, 5, 2, 1)
        val serverJsonMessage = Json.encodeToString(PlayerFinancialState.serializer(), newState)

        val mockFrame = mockk<StompFrame.Message>()
        every { mockFrame.bodyAsText } returns serverJsonMessage
        // Assicurati che il job di raccolta sia attivo
        this.coroutineContext[kotlinx.coroutines.Job]?.children?.forEach { it.join() }


        serverMessages.emit(mockFrame)

        // Diamo tempo al flusso di essere raccolto
        // Non sempre necessario con TestScope e advanceUntilIdle, ma può aiutare
        kotlinx.coroutines.yield()


        assertEquals(newState, playerSocketService.playerStateFlow.value)
    }

    @Test
    fun `sendMoneyUpdate sends correct data via session`() = runTest {
        playerSocketService.connect() // Assicurati che la sessione sia stabilita

        val playerId = "testPlayer123"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate(playerId))
        val capturedSendHeaders = slot<StompSendHeaders>()
        val capturedFrameBody = slot<FrameBody>()

        // Sovrascriviamo il mock di send per catturare gli argomenti
        coEvery { stompSession.send(capture(capturedSendHeaders), capture(capturedFrameBody)) } returns null

        playerSocketService.sendMoneyUpdate(playerId)

        coVerify { stompSession.send(any<StompSendHeaders>(), any<FrameBody>()) }
        assertEquals("/app/updateMoney", capturedSendHeaders.captured.destination)
        assertEquals(expectedJson, (capturedFrameBody.captured as FrameBody.Text).text)
    }

    @Test
    fun `connectAndSubscribe connects and sends update`() = runTest {
        val playerId = "playerSubConnect"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate(playerId))

        val capturedSubHeaders = slot<StompSubscribeHeaders>()
        val capturedSendHeaders = slot<StompSendHeaders>()
        val capturedFrameBody = slot<FrameBody>()

        // Mock per catturare gli argomenti
        coEvery { stompSession.subscribe(capture(capturedSubHeaders)) } returns serverMessages
        coEvery { stompSession.send(capture(capturedSendHeaders), capture(capturedFrameBody)) } returns null

        playerSocketService.connectAndSubscribe(playerId)

        coVerify { stompClient.connect("ws://se2-demo.aau.at:53210/ws") }
        coVerify { stompSession.subscribe(any<StompSubscribeHeaders>()) }
        assertEquals("/topic/playerMoney", capturedSubHeaders.captured.destination)

        coVerify { stompSession.send(any<StompSendHeaders>(), any<FrameBody>()) }
        assertEquals("/app/updateMoney", capturedSendHeaders.captured.destination)
        assertEquals(expectedJson, (capturedFrameBody.captured as FrameBody.Text).text)
    }

    @Test
    fun `disconnect cancels job and clears session`() = runTest {
        playerSocketService.connect() // Connetti per avere una sessione e un job
        assertNotNull(playerSocketService.getPrivateField<StompSession>("session"))
        assertNotNull(playerSocketService.getPrivateField<Job>("moneySubscriptionJob"))

        playerSocketService.disconnect()

        coVerify { stompSession.disconnect() } // Verifica la chiamata a Krossbow
        assertNull(playerSocketService.getPrivateField<StompSession>("session"))
        assertNull(playerSocketService.getPrivateField<Job>("moneySubscriptionJob")) // Il job dovrebbe essere null dopo la disconnessione
    }
}