package practices.handroll.dummy

//
interface Logger {
    fun log(message: String)
}

class SystemUnderTest(private val logger: Logger) {
    fun doSomething(input: String): String {
        // Logger is not used here
        return "Processed: $input"
    }
}
