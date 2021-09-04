package patterns.composite.v3

import java.lang.Exception

interface ErrorHandler {
    fun handle(listener: Listener, exception: Exception): Boolean
}
