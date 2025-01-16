package patterns.contract

import org.springframework.stereotype.Component

@Component
class PostgresUserRepository : UserRepository {
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