package patterns.contract

class InMemoryUserRepository : UserRepository {
    override fun create(user: User): User {
        TODO("Not yet implemented")
    }

    override fun find(id: UserId): User? {
        TODO("Not yet implemented")
    }

    override fun update(user: User): User {
        TODO("Not yet implemented")
    }

    override fun delete(id: UserId) {
        TODO("Not yet implemented")
    }
}