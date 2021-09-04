package patterns.composite.v3

import kotlinx.coroutines.runBlocking
import patterns.composite.v3.AsynchronousListener as Async
import patterns.composite.v3.CompositeListener as Composite

// start
class ConcurrentCompositeListener(
    private val listeners: List<Listener>
) : Listener {
    override fun onCreated(user: User) {
        runBlocking {
            val asyncListeners = listeners.map { Async(it, this) }
            Composite(asyncListeners).onCreated(user)
        }
    }
}
