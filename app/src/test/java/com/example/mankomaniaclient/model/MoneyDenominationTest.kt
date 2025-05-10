import com.example.mankomaniaclient.ui.model.MoneyDenomination
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MoneyDenominationTest {

    @Test
    fun `totalAmount returns correct value`() {
        val denomination = MoneyDenomination(value = 5000, quantity = 3)
        assertEquals(15000, denomination.totalAmount())
    }

    @Test
    fun `zero quantity returns zero total`() {
        val denomination = MoneyDenomination(value = 10000, quantity = 0)
        assertEquals(0, denomination.totalAmount())
    }
}