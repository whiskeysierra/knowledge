package practices.handroll.fake

//
interface UserRepository {
    fun getUser(id: Int): User?
    fun saveUser(user: User)
    fun deleteUser(id: Int)
}

data class User(val id: Int, val name: String)

class UserService(private val userRepository: UserRepository) {
    fun findUser(id: Int): String? {
        return userRepository.getUser(id)?.name
    }

    fun createUser(name: String): Int {
        // In real app, you would use a proper ID generation strategy
        val newId = (0..Int.MAX_VALUE).random()
        userRepository.saveUser(User(newId, name))
        return newId
    }

    fun removeUser(id: Int) {
        userRepository.deleteUser(id)
    }
}
