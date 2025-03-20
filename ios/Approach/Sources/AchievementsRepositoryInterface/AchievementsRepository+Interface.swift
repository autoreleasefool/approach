import AchievementsLibrary
import Dependencies
import DependenciesMacros
import ModelsLibrary

@DependencyClient
public struct AchievementsRepository: Sendable {
	public var list: @Sendable () -> AsyncThrowingStream<[Achievement.List], Error> = { .never }
	public var observeNewAchievements: @Sendable () -> AsyncStream<Achievement.Summary> = { .never }
}

extension AchievementsRepository: TestDependencyKey {
	public static var testValue: Self { Self() }
}
