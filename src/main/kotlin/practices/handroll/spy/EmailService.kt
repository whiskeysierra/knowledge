package practices.handroll.spy

//
interface EmailService {
    fun sendEmail(recipient: String, message: String)
}

class UserNotifier(private val emailService: EmailService) {
    fun notifyUser(userEmail: String, message: String) {
        emailService.sendEmail(userEmail, message)
        emailService.sendEmail("admin@example.com", "User notified") // Send notification to admin as well
    }
}
