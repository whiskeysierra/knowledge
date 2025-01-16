package patterns.contract

data class UserId(private val value: String)
data class User(val id: UserId, val name: String)

//
interface UserRepository {
    fun create(user: User): User
    fun find(id: UserId): User?
    fun update(user: User): User
    fun delete(id: UserId)
}
