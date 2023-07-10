import AnalyticsServiceInterface
import ConstantsLibrary
import Dependencies
import Foundation
import TelemetryClient

extension AnalyticsService: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.analyticsGameSessionId) var analyticsGameSessionId
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
				let payload = (await properties.globalProperties).merging(event.payload ?? [:]) { first, _ in first }

				if let sessionEvent = event as? GameSessionTrackableEvent,
					 !(await properties.shouldRecordEvent(sessionEvent.eventId, toSession: analyticsGameSessionId)) {
					return
				}

				TelemetryManager.send(event.name, with: payload)
			}
		)
	}()
}

actor PropertyManager {
	var globalProperties: [String: String] = [:]
	var sessions: [UUID: Set<UUID>] = [:]

	func setProperty(value: String, forKey: String) {
		globalProperties[forKey] = value
	}

	func removeProperty(forKey: String) {
		globalProperties[forKey] = nil
	}

	func shouldRecordEvent(_ id: UUID, toSession: UUID) -> Bool {
		if sessions[toSession] == nil {
			sessions[toSession] = []
		}

		guard let (inserted, _) = sessions[toSession]?.insert(id) else { return false }
		return inserted
	}
}
