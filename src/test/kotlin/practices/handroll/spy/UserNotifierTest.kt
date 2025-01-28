package practices.handroll.spy

//
import io.github.whiskeysierra.testdoubles.spy.Tracker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserNotifierTest {
    @Test
    fun `sends emails to user and admin`() {
        val spy = SpyEmailService()
        val notifier = UserNotifier(spy)

        val sentEmails: Tracker<Pair<String, String>> = spy.track()

        notifier.notifyUser("test@example.com", "Hello!")

        assertThat(sentEmails).containsExactly(
            Pair("test@example.com", "Hello!"),
            Pair("admin@example.com", "User notified")
        )
    }
}
