import AchievementsLibrary
import Dependencies
import DependenciesMacros
import ModelsLibrary

@DependencyClient
public struct AchievementsRepository: Sendable {
	public var list: @Sendable () -> AsyncThrowingStream<[Achievement.Counted], Error> = { .never }
	public var observeNewAchievements: @Sendable () -> AsyncStream<EarnableAchievement> = { .never }
}

extension AchievementsRepository: TestDependencyKey {
	public static var testValue: Self { Self() }
}
