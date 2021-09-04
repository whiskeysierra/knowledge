package patterns.composite.v3

// start
class CompositeListener(
    private val listeners: Collection<Listener>
) : Listener {
    override fun onCreated(user: User) {
        listeners.forEach { it.onCreated(user) }
    }
}
