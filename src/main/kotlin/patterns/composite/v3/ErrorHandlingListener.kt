package patterns.composite.v3

//
fun Listener.onError(handler: ErrorHandler) =
    object : Listener {
        override fun onCreated(user: User) {
            try {
                this@onError.onCreated(user)
            } catch (e: Exception) {
                if (!handler.handle(this@onError, e)) return
            }
        }
    }
