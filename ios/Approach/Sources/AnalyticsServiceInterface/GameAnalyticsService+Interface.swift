import Dependencies

public struct GameAnalyticsService: Sendable {
	public var recordGameEvent: @Sendable (Event, String) async -> Void
	public var resetSession: @Sendable () async -> Void

	public init(
		recordGameEvent: @escaping @Sendable (Event, String) async -> Void,
		resetSession: @escaping @Sendable () async -> Void
	) {
		self.recordGameEvent = recordGameEvent
		self.resetSession = resetSession
	}

	public func didView(gameId: String) async {
		await recordGameEvent(.viewed, gameId)
	}

	public func didUpdate(gameId: String) async {
		await recordGameEvent(.updated, gameId)
	}

	public func didSetManualScore(forGameId: String) async {
		await recordGameEvent(.manualScoreSet, forGameId)
	}
}

extension GameAnalyticsService {
	public enum Event {
		case viewed
		case updated
		case manualScoreSet
	}
}

extension GameAnalyticsService: TestDependencyKey {
	public static var testValue = Self(
		recordGameEvent: { _, _ in unimplemented("\(Self.self).recordGameEvent") },
		resetSession: { unimplemented("\(Self.self).resetSession") }
	)
}

extension DependencyValues {
	public var gameAnalytics: GameAnalyticsService {
		get { self[GameAnalyticsService.self] }
		set { self[GameAnalyticsService.self] = newValue }
	}
}

