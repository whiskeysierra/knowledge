package practices.handroll.spy

//
import io.github.whiskeysierra.testdoubles.spy.OutputTracking
import io.github.whiskeysierra.testdoubles.spy.Tracker
import org.assertj.core.util.VisibleForTesting

class SpyEmailService : EmailService {
    private val tracking = OutputTracking<Pair<String, String>>()

    @VisibleForTesting
    fun track(): Tracker<Pair<String, String>> = tracking.track()

    override fun sendEmail(recipient: String, message: String) {
        tracking.add(Pair(recipient, message))
    }
}
