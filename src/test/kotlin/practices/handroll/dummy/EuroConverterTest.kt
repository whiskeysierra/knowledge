package practices.handroll.dummy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

//
class EuroConverterTest {
    @Test
    fun `converts to eur ignoring preferred currency`() {
        val converter = EuroConverter()
        val amount = Money(BigDecimal("100"), USD)

        val convertedAmount = converter.convert(amount, DummyCurrency)

        assertEquals(BigDecimal("92.00"), convertedAmount.amount)
        assertEquals("EUR", convertedAmount.currency.code)
    }
}
