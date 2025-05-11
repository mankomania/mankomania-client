import android.content.Intent
import com.example.mankomaniaclient.extractPlayerId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StartingMoneyUtilsTest {

    @Test
    fun `returns playerId when present in intent`() {
        val intent = mockk<Intent>()
        every { intent.getStringExtra("playerId") } returns "mock-id-456"

        val result = extractPlayerId(intent)
        assertEquals("mock-id-456", result)
    }
}