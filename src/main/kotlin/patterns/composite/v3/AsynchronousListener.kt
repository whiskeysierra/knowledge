package patterns.composite.v3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// start
class AsynchronousListener(
    private val listener: Listener,
    private val scope: CoroutineScope,
) : Listener {
    override fun onCreated(user: User) {
        scope.launch { listener.onCreated(user) }
    }
}
