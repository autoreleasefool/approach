import AnalyticsServiceInterface
import Dependencies

extension GameAnalyticsService: DependencyKey {
	public static var liveValue: Self = live()
	public static func live() -> Self {
		@Dependency(\.analytics) var analytics
		let store = GameAnalyticsPropertiesStore()

		return Self(
			recordGameEvent: { event, id in
				guard await store.shouldRecordAction(event, forGameId: id) else { return }
				await analytics.trackEvent(event.trackableEvent)
			},
			resetSession: {
				await store.reset()
			}
		)
	}
}

actor GameAnalyticsPropertiesStore {
	private var gameActionsTaken: [String: Set<GameAnalyticsService.Event>] = [:]

	func shouldRecordAction(_ action: GameAnalyticsService.Event, forGameId: String) -> Bool {
		if !gameActionsTaken.keys.contains(forGameId) {
			gameActionsTaken[forGameId] = []
		}

		guard let (updated, _) = gameActionsTaken[forGameId]?.insert(action) else { return false }
		return updated
	}

	func reset() {
		gameActionsTaken.removeAll()
	}
}

extension GameAnalyticsService.Event {
	var trackableEvent: TrackableEvent {
		switch self {
		case .viewed:
			return Analytics.Game.Viewed()
		case .updated:
			return Analytics.Game.Updated()
		case .manualScoreSet:
			return Analytics.Game.ManualScoreSet()
		}
	}
}
