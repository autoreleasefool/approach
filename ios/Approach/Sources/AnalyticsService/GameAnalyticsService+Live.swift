import AnalyticsPackageServiceInterface
import AnalyticsServiceInterface
import Dependencies
import Foundation

extension GameAnalyticsService: DependencyKey {
	public static var liveValue: Self {
		let sessionID = LockIsolated<UUID?>(nil)
		let sessions = LockIsolated<[UUID: Set<UUID>]>([:])

		return Self(
			trackEvent: { event in
				if let session = sessionID.value {
					let inserted = sessions.withValue {
						$0[session, default: []].insert(event.eventId).inserted
					}

					if !inserted {
						return
					}
				}

				@Dependency(\.analytics) var analytics
				let basicEvent = BasicEvent(name: event.name, payload: event.payload)
				try? await analytics.trackEvent(basicEvent)
			},
			resetGameSessionID: {
				@Dependency(\.uuid) var uuid
				sessionID.setValue(uuid())
			}
		)
	}
}

private struct BasicEvent: TrackableEvent {
	let name: String
	let payload: [String: String]?
}
