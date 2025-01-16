package patterns.composite.v2

//
class UserService(
    private val repository: UserRepository,
    private val listeners: List<Listener>
) {

    fun create(user: User) {
        require(user.isValid())
        val created = repository.create(user)
        listeners.forEach { it.onCreated(created) }
    }
}

