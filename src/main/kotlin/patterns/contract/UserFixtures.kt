package patterns.contract

import java.util.UUID.randomUUID

fun randomUser() =
    User(id = UserId(randomUUID().toString()), name = "D. Fault")