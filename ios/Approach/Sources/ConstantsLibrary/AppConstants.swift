import Foundation

public enum AppConstants {
	public static let appName = "Approach"
	public static let openSourceRepositoryUrl = URL(string: "https://github.com/autoreleasefool/approach")!
}

extension AppConstants {
	public enum ApiKey {
		public static let disable = "DISABLE"

		public static let telemetryDeck: String = {
			(try? Configuration.value(for: "TELEMETRY_DECK_API_KEY")) ?? AppConstants.ApiKey.disable
		}()
	}
}
