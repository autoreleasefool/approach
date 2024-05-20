import AnalyticsServiceInterface
import Dependencies
import Sentry

extension CrashGenerator: DependencyKey {
	public static var liveValue: Self {
		return Self { SentrySDK.crash() }
	}
}
