package practices.handroll.dummy

//
interface CurrencyConverter {
    fun convert(amount: Money, preferredCurrency: Currency): Money
}