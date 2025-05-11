import com.example.mankomaniaclient.network.PlayerSocketService
import com.example.mankomaniaclient.ui.model.PlayerFinancialState
import com.example.mankomaniaclient.ui.model.PlayerMoneyUpdate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerSocketServiceTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private lateinit var stompClient: StompClient
    private lateinit var stompSession: StompSession
    private lateinit var playerSocketService: PlayerSocketService
    private lateinit var moneyFlow: MutableSharedFlow<StompFrame.Message>

    @BeforeEach
    fun setup() = runTest(testDispatcher) {
        stompClient = mockk()
        stompSession = mockk(relaxed = true)
        moneyFlow = MutableSharedFlow(extraBufferCapacity = 1)

        coEvery { stompClient.connect(any()) } returns stompSession
        coEvery { stompSession.subscribe(any()) } returns moneyFlow
        coEvery { stompSession.send(any<StompSendHeaders>(), any<FrameBody.Text>()) } returns mockk()

        playerSocketService = PlayerSocketService(stompClient, this)
    }

    @Test
    fun `sendMoneyUpdate should send correct JSON`() = runTest(testDispatcher) {
        val playerId = "valentina"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate(playerId))

        playerSocketService.connect()
        testScheduler.advanceUntilIdle()

        playerSocketService.sendMoneyUpdate(playerId)

        coVerify {
            stompSession.send(
                match { it.destination == "/app/updateMoney" },
                FrameBody.Text(expectedJson)
            )
        }
    }

    @Test
    fun `connectAndSubscribe should connect and send update`() = runTest(testDispatcher) {
        val playerId = "test123"
        val expectedJson = Json.encodeToString(PlayerMoneyUpdate(playerId))

        playerSocketService.connectAndSubscribe(playerId)
        testScheduler.advanceUntilIdle()

        coVerify { stompClient.connect(any()) }
        coVerify { stompSession.send(
            match { it.destination == "/app/updateMoney" },
            FrameBody.Text(expectedJson)
        ) }
    }

    @Test
    fun `disconnect should close session`() = runTest(testDispatcher) {
        playerSocketService.connect()
        testScheduler.advanceUntilIdle()

        playerSocketService.disconnect()
        coVerify { stompSession.disconnect() }
    }

    @Test
    fun `initial state should be zeroed`() = runTest(testDispatcher) {
        val state = playerSocketService.playerStateFlow.value
        assertEquals(PlayerFinancialState(0, 0, 0, 0), state)
    }
}
