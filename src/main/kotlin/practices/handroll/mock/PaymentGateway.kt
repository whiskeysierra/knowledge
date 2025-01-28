package practices.handroll.mock

//
interface PaymentGateway {
    fun processPayment(amount: Double): Boolean
}

class OrderProcessor(private val paymentGateway: PaymentGateway) {
    fun processOrder(amount: Double) {
        if (!paymentGateway.processPayment(amount)) {
            throw PaymentFailedException("Payment failed for amount: $amount")
        }
        // Order processing logic here
    }
}

class PaymentFailedException(message: String) : RuntimeException(message)
