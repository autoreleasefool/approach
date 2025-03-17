import AchievementsLibrary
import Dependencies
import DependenciesMacros

@DependencyClient
public struct AchievementsService: Sendable {
	public var sendEvent: @Sendable (ConsumableAchievementEvent) async -> Void
}

extension AchievementsService: TestDependencyKey {
	public static var testValue: Self { Self() }
}
