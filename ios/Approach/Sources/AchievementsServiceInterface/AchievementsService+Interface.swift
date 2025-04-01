import AchievementsLibrary
import Dependencies
import DependenciesMacros

@DependencyClient
public struct AchievementsService: Sendable {
	public var sendEvent: @Sendable (ConsumableAchievementEvent) async -> Void
	public var hasEarnedAchievement: @Sendable (EarnableAchievement.Type) async -> Bool = { _ in false }
}

extension AchievementsService: TestDependencyKey {
	public static var testValue: Self { Self() }
}
