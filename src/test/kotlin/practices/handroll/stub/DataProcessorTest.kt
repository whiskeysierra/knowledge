package practices.handroll.stub

import org.junit.jupiter.api.Assertions.*

import io.github.whiskeysierra.testdoubles.stub.ConfigurableResponses.once
import org.junit.jupiter.api.Test

//
class DataProcessorTest {
    @Test
    fun `retrieves and processes data`() {
        val provider = StubDataProvider(responses = once("Test Data", null, "Other Data"))
        val processor = DataProcessor(provider)

        assertEquals("Processed: Test Data", processor.process(1))
        assertEquals("Processed: Default Data", processor.process(2))
        assertEquals("Processed: Other Data", processor.process(3))
    }

    @Test
    fun `uses fallback when no data is available`() {
        val provider = StubDataProvider(responses = once(null, "Real Data"))
        val processor = DataProcessor(provider)

        assertEquals("Processed: Fallback Data", processor.process(4, "Fallback Data"))
        assertEquals("Processed: Real Data", processor.process(5, "Fallback Data"))
    }
}