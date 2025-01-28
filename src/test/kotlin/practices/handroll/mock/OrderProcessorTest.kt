package practices.handroll.mock

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

//
class OrderProcessorTest {

    @Test
    fun `processOrder successfully processes payment`() {
        val mock = MockPaymentGateway()
        mock.willReturn(true)
        val processor = OrderProcessor(mock)
        processor.processOrder(100.0)
        assert(mock.wasCalled)
    }

    @Test
    fun `processOrder throws exception when payment fails`() {
        val mock = MockPaymentGateway()
        mock.willReturn(false)
        val processor = OrderProcessor(mock)

        assertThrows<PaymentFailedException> {
            processor.processOrder(50.0)
        }
        assert(mock.wasCalled)
    }

    @Test
    fun `processOrder checks the amount passed to the payment gateway`() {
        val mock = MockPaymentGateway()
        mock.expectPaymentOf(200.0)
        mock.willReturn(true)
        val processor = OrderProcessor(mock)
        processor.processOrder(200.0)
        assert(mock.wasCalled)
    }

    @Test
    fun `processOrder throws assertion if unexpected amount passed`() {
        val mock = MockPaymentGateway()
        mock.expectPaymentOf(200.0)
        val processor = OrderProcessor(mock)
        assertThrows<AssertionError> {
            processor.processOrder(100.0)
        }
        assert(mock.wasCalled)
    }
}