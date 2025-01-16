package patterns.composite.v3

//
class Usage {
    fun userService(repository: UserRepository): UserService {
        return UserService(
            repository,
            CompositeListener(listOf(
                LoggingListener().onError(Log),
                MetricsListener().onError(Ignore),
            )).concurrent()
        )
    }
}
