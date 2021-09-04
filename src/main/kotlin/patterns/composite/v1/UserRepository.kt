package patterns.composite.v1

interface UserRepository {
    fun create(user: User): User
}
