import Foundation

public enum AppConstants {
	public static let appName = "Bowling Companion"
	public static let openSourceRepositoryUrl = URL(string: "https://github.com/autoreleasefool/bowling-companion-ios")!
}

extension AppConstants {
	public enum ApiKey {
		public static let disable = "DISABLE"

		public static let telemetryDeck: String = {
			(try? Configuration.value(for: "TELEMETRY_DECK_API_KEY")) ?? AppConstants.ApiKey.disable
		}()
	}
}
