import AnalyticsServiceInterface
import ConstantsLibrary
import Dependencies
import TelemetryClient

extension AnalyticsService: DependencyKey {
	public static let liveValue = Self(
		initialize: {
			let apiKey = AppConstants.ApiKey.telemetryDeck
			let configuration = TelemetryManagerConfiguration(appID: apiKey)
			if apiKey == AppConstants.ApiKey.disable {
				print("Analytics disabled")
				configuration.analyticsDisabled = true
			}

			TelemetryManager.initialize(with: configuration)
		},
		trackEvent: {
			TelemetryManager.send($0.name, with: $0.payload ?? [:])
		}
	)
}
