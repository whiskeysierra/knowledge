package patterns.composite.v3

import java.lang.Exception

// start
interface ErrorHandler {
    fun handle(listener: Listener, exception: Exception): Boolean
}
