package patterns.composite.v3

// start
class ErrorHandlingListener(
    private val listener: Listener,
    private val handler: ErrorHandler,
) : Listener {
    override fun onCreated(user: User) {
        try {
            listener.onCreated(user)
        } catch (e: Exception) {
            if (!handler.handle(listener, e)) return
        }
    }
}
