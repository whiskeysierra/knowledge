package practices.handroll.mock

//
class MockPaymentGateway : PaymentGateway {
    private var paymentProcessed = false
    private var expectedAmount: Double? = null
    var wasCalled = false

    fun willReturn(value: Boolean) {
        paymentProcessed = value
    }

    fun expectPaymentOf(amount: Double) {
        expectedAmount = amount
    }

    override fun processPayment(amount: Double): Boolean {
        wasCalled = true
        if (expectedAmount != null && expectedAmount != amount) {
            throw AssertionError("Expected payment of $expectedAmount but received $amount")
        }
        return paymentProcessed
    }
}
