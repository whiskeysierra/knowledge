package patterns.composite.v2

interface UserRepository {
    fun create(user: User): User
}
