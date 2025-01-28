package practices.handroll.fake

//
class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<Int, User>()

    override fun getUser(id: Int): User? {
        return users[id]
    }

    override fun saveUser(user: User) {
        users[user.id] = user
    }

    override fun deleteUser(id: Int) {
        users.remove(id)
    }
}
