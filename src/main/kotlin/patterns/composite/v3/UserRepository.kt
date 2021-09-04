package patterns.composite.v3

interface UserRepository {
    fun create(user: User): User
}
