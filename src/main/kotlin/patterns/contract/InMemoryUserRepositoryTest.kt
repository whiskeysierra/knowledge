package patterns.contract

//
class InMemoryUserRepositoryTest : UserRepositoryContract {
    override val unit = InMemoryUserRepository()
}