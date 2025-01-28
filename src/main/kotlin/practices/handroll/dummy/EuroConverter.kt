package practices.handroll.dummy

import java.math.BigDecimal

//
/**
 * Converts amounts to EUR.  Ignores the [preferredCurrency] parameter.
 */
class EuroConverter : CurrencyConverter {
    override fun convert(amount: Money, preferredCurrency: Currency): Money {
        val euroAmount = when (amount.currency.code) {
            "USD" -> amount.amount.multiply(BigDecimal("0.92"))
            "GBP" -> amount.amount.multiply(BigDecimal("1.15"))
            else -> amount.amount
        }
        return Money(euroAmount, EUR)
    }

}
