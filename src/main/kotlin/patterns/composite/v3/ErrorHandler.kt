package patterns.composite.v3

import java.lang.Exception

//
interface ErrorHandler {
    fun handle(listener: Listener, exception: Exception): Boolean
}

object Log : ErrorHandler {
    override fun handle(listener: Listener, exception: Exception): Boolean {
        TODO("Not yet implemented")
    }
}

object Ignore : ErrorHandler {
    override fun handle(listener: Listener, exception: Exception): Boolean = true
}
