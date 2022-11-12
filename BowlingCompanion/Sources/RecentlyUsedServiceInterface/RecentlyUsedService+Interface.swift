import Dependencies
import Foundation

public enum ResourceCategory: String {
	case bowlers
	case leagues
	case frames
	case alleys
}

public struct RecentlyUsedService: Sendable {
	public var didRecentlyUseResource: @Sendable (ResourceCategory, UUID) -> Void
	public var observeRecentlyUsed: @Sendable (ResourceCategory) -> AsyncStream<[UUID]>
	public var resetRecentlyUsed: @Sendable (ResourceCategory) -> Void

	public init(
		didRecentlyUseResource: @escaping @Sendable (ResourceCategory, UUID) -> Void,
		observeRecentlyUsed: @escaping @Sendable (ResourceCategory) -> AsyncStream<[UUID]>,
		resetRecentlyUsed: @escaping @Sendable (ResourceCategory) -> Void
	) {
		self.didRecentlyUseResource = didRecentlyUseResource
		self.observeRecentlyUsed = observeRecentlyUsed
		self.resetRecentlyUsed = resetRecentlyUsed
	}
}

extension RecentlyUsedService: TestDependencyKey {
	public static var testValue = Self(
		didRecentlyUseResource: { _, _ in fatalError("\(Self.self).didRecentlyUseResource") },
		observeRecentlyUsed: { _ in fatalError("\(Self.self).observeRecentlyUsed") },
		resetRecentlyUsed: { _ in fatalError("\(Self.self).resetRecentlyUsed") }
	)
}

extension DependencyValues {
	public var recentlyUsedService: RecentlyUsedService {
		get { self[RecentlyUsedService.self] }
		set { self[RecentlyUsedService.self] = newValue }
	}
}
