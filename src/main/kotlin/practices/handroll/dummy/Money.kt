package practices.handroll.dummy

import java.math.BigDecimal

//
data class Money(val amount: BigDecimal, val currency: Currency)

data object EUR : Currency {
    override val code: String = "EUR"
    override val symbol: String = "€"
    override val displayName: String = "Euro"
    override val defaultFractionDigits: Int = 2
}

data object USD : Currency {
    override val code = "USD";
    override val symbol = "$";
    override val displayName = "US Dollar";
    override val defaultFractionDigits = 2
}