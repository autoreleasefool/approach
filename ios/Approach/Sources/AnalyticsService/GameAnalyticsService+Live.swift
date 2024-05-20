import AnalyticsPackageServiceInterface
import AnalyticsServiceInterface
import Dependencies
import Foundation

extension GameAnalyticsService: DependencyKey {
	public static var liveValue: Self {
		let sessionID = ActorIsolated<UUID?>(nil)
		let sessions = ActorIsolated<[UUID: Set<UUID>]>([:])

		return Self(
			trackEvent: { event in
				if let session = await sessionID.value {
					let inserted = await sessions.withValue {
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
				await sessionID.setValue(uuid())
			}
		)
	}
}

private struct BasicEvent: TrackableEvent {
	let name: String
	let payload: [String: String]?
}
