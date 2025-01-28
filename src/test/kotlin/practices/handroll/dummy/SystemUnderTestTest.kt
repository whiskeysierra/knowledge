package practices.handroll.dummy

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

//
class SystemUnderTestTest {
    @Test
    fun `processes input`() {
        val sut = SystemUnderTest(logger = DummyLogger)
        val result = sut.doSomething("test")
        assertEquals("Processed: test", result)
    }
}
