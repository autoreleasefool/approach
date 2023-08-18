import Dependencies
import EmailServiceInterface
import MessageUI

extension EmailService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			canSendEmail: { @MainActor in
				MFMailComposeViewController.canSendMail()
			}
		)
	}()
}
