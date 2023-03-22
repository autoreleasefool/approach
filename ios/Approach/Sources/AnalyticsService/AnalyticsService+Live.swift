import AnalyticsServiceInterface
import ConstantsLibrary
import Dependencies
import TelemetryClient

extension AnalyticsService: DependencyKey {
	public static var liveValue: Self = {
		let properties = PropertyManager()

		return Self(
			initialize: {
				let apiKey = AppConstants.ApiKey.telemetryDeck
				let configuration = TelemetryManagerConfiguration(appID: apiKey)
				if apiKey == AppConstants.ApiKey.disable {
					print("Analytics disabled")
					configuration.analyticsDisabled = true
				}

				TelemetryManager.initialize(with: configuration)
			},
			setGlobalProperty: { value, key in
				if let value {
					await properties.setProperty(value: value, forKey: key)
				} else {
					await properties.removeProperty(forKey: key)
				}
			},
			trackEvent: { event in
				Task.detached {
					let payload = (await properties.globalProperties).merging(event.payload ?? [:]) { first, _ in first }
					TelemetryManager.send(event.name, with: payload)
				}
			}
		)
	}()
}
