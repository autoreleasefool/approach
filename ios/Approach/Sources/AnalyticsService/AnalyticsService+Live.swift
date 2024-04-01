import AnalyticsServiceInterface
import ConstantsLibrary
import Dependencies
import ErrorHandlerLibrary
import Foundation
import PreferenceServiceInterface
import Sentry
import TelemetryClient

extension AnalyticsService: DependencyKey {
	public static var liveValue: Self = {
		let properties = PropertyManager()

		let gameSessionID: LockIsolated<UUID?> = .init(nil)

		@Sendable func getOptInStatus() -> Analytics.OptInStatus {
			@Dependency(PreferenceService.self) var preferences
			return Analytics.OptInStatus(rawValue: preferences.string(forKey: .analyticsOptInStatus) ?? "") ?? .optedIn
		}

		@Sendable func initialize() {
			let apiKey = AppConstants.ApiKey.telemetryDeck
			let configuration = TelemetryManagerConfiguration(appID: apiKey)
			if apiKey.isEmpty {
				print("Analytics disabled")
				configuration.analyticsDisabled = true
			} else if getOptInStatus() == .optedOut {
				print("Analytics opted out")
				configuration.analyticsDisabled = true
			}

			TelemetryManager.initialize(with: configuration)
		}

		return Self(
			initialize: initialize,
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
					 let gameSessionID = gameSessionID.value,
					 !(await properties.shouldRecordEvent(sessionEvent.eventId, toSession: gameSessionID)) {
					return
				}

				TelemetryManager.send(event.name, with: payload)
			},
			breadcrumb: { breadcrumb in
				let crumb = Sentry.Breadcrumb(level: .info, category: breadcrumb.category.rawValue)
				crumb.message = breadcrumb.message
				SentrySDK.addBreadcrumb(crumb)
			},
			resetGameSessionID: {
				@Dependency(\.uuid) var uuid
				gameSessionID.setValue(uuid())
			},
			getOptInStatus: getOptInStatus,
			setOptInStatus: { newValue in
				@Dependency(PreferenceService.self) var preferences
				preferences.setKey(.analyticsOptInStatus, toString: newValue.rawValue)

				TelemetryManager.terminate()
				initialize()

				return getOptInStatus()
			},
			forceCrash: {
				SentrySDK.crash()
			},
			captureException: { error in
				ErrorHandler.capture(error: error)
			},
			captureErrorMessage: { message in
				ErrorHandler.capture(message: message)
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
