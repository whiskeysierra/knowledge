package patterns.composite.v3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// start
fun Listener.async(scope: CoroutineScope): Listener =
    object : Listener {
        override fun onCreated(user: User) {
            scope.launch { this@async.onCreated(user) }
        }
    }
