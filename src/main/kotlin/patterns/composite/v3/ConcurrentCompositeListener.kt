package patterns.composite.v3

import kotlinx.coroutines.runBlocking

// start
class ConcurrentCompositeListener(
    private val listeners: Collection<Listener>
) : Listener {
    override fun onCreated(user: User) {
        runBlocking {
            val async = listeners.map { AsyncListener(it, this) }
            CompositeListener(async).onCreated(user)
        }
    }
}
