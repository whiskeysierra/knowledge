package patterns.composite.v1

//
class UserService(
    private val repository: UserRepository,
    private val listener: Listener
) {

    fun create(user: User) {
        require(user.isValid())
        val created = repository.create(user)
        listener.onCreated(created)
    }
}

