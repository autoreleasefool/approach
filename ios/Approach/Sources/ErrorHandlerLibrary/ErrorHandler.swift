import Foundation
import Sentry

public struct ErrorHandler {
	public static func capture(error: any Error) {
		SentrySDK.capture(error: error)
	}

	public static func capture(message: String) {
		SentrySDK.capture(message: message)
	}
}
