import AnalyticsPackageServiceInterface
import Dependencies
import DependenciesMacros

@DependencyClient
public struct GameAnalyticsService: Sendable {
	public var trackEvent: @Sendable (GameSessionTrackableEvent) async -> Void
	public var resetGameSessionID: @Sendable () async -> Void
}

extension GameAnalyticsService: TestDependencyKey {
	public static var testValue = Self()
}

extension DependencyValues {
	public var gameAnalytics: GameAnalyticsService {
		get { self[GameAnalyticsService.self] }
		set { self[GameAnalyticsService.self] = newValue }
	}
}
