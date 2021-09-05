package patterns.composite.v3

import kotlinx.coroutines.runBlocking

// start
fun CompositeListener.concurrent() =
    object : Listener {
        override fun onCreated(user: User) {
            runBlocking {
                val async = listeners.map { it.async(this) }
                CompositeListener(async).onCreated(user)
            }
        }
    }
