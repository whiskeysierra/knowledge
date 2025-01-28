package practices.handroll.fake

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

//
class UserServiceTest {

    @Test
    fun `finds user if user exists`() {
        val repository = InMemoryUserRepository()
        val userService = UserService(repository)
        val testUser = User(1, "Test User")
        repository.saveUser(testUser)

        val userName = userService.findUser(1)
        assertEquals("Test User", userName)
    }

    @Test
    fun `doesn't find user if user does not exist`() {
        val repository = InMemoryUserRepository()
        val userService = UserService(repository)

        val userName = userService.findUser(999)
        assertNull(userName)
    }

    @Test
    fun `creates the user`() {
        val repository = InMemoryUserRepository()
        val userService = UserService(repository)

        val newUserId = userService.createUser("New User")
        val retrievedUser = repository.getUser(newUserId)

        assertEquals("New User", retrievedUser?.name)
    }

    @Test
    fun `deletes the user`() {
        val repository = InMemoryUserRepository()
        val userService = UserService(repository)
        val testUser = User(1, "Test User")
        repository.saveUser(testUser)

        userService.removeUser(1)

        val retrievedUser = repository.getUser(1)
        assertNull(retrievedUser)
    }
}
