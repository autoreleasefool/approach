import BadgesLibrary
import Dependencies
import DependenciesMacros

@DependencyClient
public struct BadgesService: Sendable {
	public var observeNewBadges: @Sendable () -> AsyncStream<EarnableBadge> = { .never }
	public var allEarnedBadges: @Sendable () -> AsyncThrowingStream<[EarnableBadge], Error> = { .never }
	public var sendEvent: @Sendable (ConsumableBadgeEvent) async -> Void
}

extension BadgesService: TestDependencyKey {
	public static var testValue: Self { Self() }
}
