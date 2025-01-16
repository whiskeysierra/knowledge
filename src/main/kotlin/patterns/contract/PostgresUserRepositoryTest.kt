package patterns.contract

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import patterns.contract.PostgresUserRepositoryTest.Initializer

@Component
//
@SpringBootTest
@ContextConfiguration(initializers = [Initializer::class])
@Testcontainers
class PostgresUserRepositoryTest : UserRepositoryContract {

    companion object {
        @Container
        @ServiceConnection
        val postgres = Postgres()
    }


    internal class Initializer : PostgresInitializer(postgres)

    @Import(PostgresUserRepository::class)
    @Configuration
    @ImportAutoConfiguration(
        classes = [
            DataSourceAutoConfiguration::class,
            FlywayAutoConfiguration::class,
            JdbcTemplateAutoConfiguration::class,
        ],
    )
    open class TestConfiguration

    @Autowired
    override lateinit var unit: PostgresUserRepository

    @Autowired
    private lateinit var jdbc: NamedParameterJdbcTemplate


    @BeforeEach
    @AfterEach
    fun truncate() {
        jdbc.update("TRUNCATE my_table", emptyMap<String, Any>())
    }
}

class Postgres : PostgreSQLContainer<Postgres>("postgres:16")

abstract class PostgresInitializer(private val postgres: Postgres) :
    ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.flyway.user: ${postgres.username}",
            "spring.flyway.password: ${postgres.password}",
            "spring.datasource.url: ${postgres.jdbcUrl}",
            "spring.datasource.username: user",
            "spring.datasource.password: password",
        ).applyTo(configurableApplicationContext.environment)
    }
}
