package patterns.composite.v3

//
class CompositeListener(
    internal val listeners: Collection<Listener>,
) : Listener {
    override fun onCreated(user: User) {
        listeners.forEach { it.onCreated(user) }
    }
}
