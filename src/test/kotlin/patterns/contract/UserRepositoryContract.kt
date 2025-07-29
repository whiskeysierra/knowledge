package patterns.contract

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.UUID.randomUUID

//
interface UserRepositoryContract {

    val unit: UserRepository

    @Test
    fun `finds user`() {
        val expected = unit.create(randomUser())
        val actual = unit.find(expected.id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `won't find absent user`() {
        val user = unit.find(UserId(randomUUID().toString()))
        assertThat(user).isNull()
    }
}