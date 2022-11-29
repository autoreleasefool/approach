import Dependencies
import Foundation

public enum RecentlyUsedResource: String {
	case bowlers
	case leagues
	case frames
	case alleys
	case gear
}

public struct RecentlyUsedService: Sendable {
	public var didRecentlyUseResource: @Sendable (RecentlyUsedResource, UUID) -> Void
	public var getRecentlyUsed: @Sendable (RecentlyUsedResource) -> [UUID]
	public var observeRecentlyUsed: @Sendable (RecentlyUsedResource) -> AsyncStream<[UUID]>
	public var resetRecentlyUsed: @Sendable (RecentlyUsedResource) -> Void

	public init(
		didRecentlyUseResource: @escaping @Sendable (RecentlyUsedResource, UUID) -> Void,
		getRecentlyUsed: @escaping @Sendable (RecentlyUsedResource) -> [UUID],
		observeRecentlyUsed: @escaping @Sendable (RecentlyUsedResource) -> AsyncStream<[UUID]>,
		resetRecentlyUsed: @escaping @Sendable (RecentlyUsedResource) -> Void
	) {
		self.didRecentlyUseResource = didRecentlyUseResource
		self.getRecentlyUsed = getRecentlyUsed
		self.observeRecentlyUsed = observeRecentlyUsed
		self.resetRecentlyUsed = resetRecentlyUsed
	}
}

extension RecentlyUsedService: TestDependencyKey {
	public static var testValue = Self(
		didRecentlyUseResource: { _, _ in fatalError("\(Self.self).didRecentlyUseResource") },
		getRecentlyUsed: { _ in fatalError("\(Self.self).getRecentlyUsed") },
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
