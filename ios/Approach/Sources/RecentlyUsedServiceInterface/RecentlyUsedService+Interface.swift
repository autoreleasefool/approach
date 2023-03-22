import Dependencies
import Foundation

public struct RecentlyUsedService: Sendable {
	public var didRecentlyUseResource: @Sendable (Resource, UUID) -> Void
	public var getRecentlyUsed: @Sendable (Resource) -> [Entry]
	public var observeRecentlyUsed: @Sendable (Resource) -> AsyncStream<[Entry]>
	public var resetRecentlyUsed: @Sendable (Resource) -> Void

	public init(
		didRecentlyUseResource: @escaping @Sendable (Resource, UUID) -> Void,
		getRecentlyUsed: @escaping @Sendable (Resource) -> [Entry],
		observeRecentlyUsed: @escaping @Sendable (Resource) -> AsyncStream<[Entry]>,
		resetRecentlyUsed: @escaping @Sendable (Resource) -> Void
	) {
		self.didRecentlyUseResource = didRecentlyUseResource
		self.getRecentlyUsed = getRecentlyUsed
		self.observeRecentlyUsed = observeRecentlyUsed
		self.resetRecentlyUsed = resetRecentlyUsed
	}
}

extension RecentlyUsedService {
	public enum Resource: String {
		case bowlers
		case leagues
		case frames
		case alleys
		case gear
		case teams
		case opponents
	}
}

extension RecentlyUsedService {
	public struct Entry: Codable, Equatable {
		public let id: UUID
		public let lastUsedAt: Date

		public init(id: UUID, lastUsedAt: Date) {
			self.id = id
			self.lastUsedAt = lastUsedAt
		}
	}
}

extension RecentlyUsedService: TestDependencyKey {
	public static var testValue = Self(
		didRecentlyUseResource: { _, _ in unimplemented("\(Self.self).didRecentlyUseResource") },
		getRecentlyUsed: { _ in unimplemented("\(Self.self).getRecentlyUsed") },
		observeRecentlyUsed: { _ in unimplemented("\(Self.self).observeRecentlyUsed") },
		resetRecentlyUsed: { _ in unimplemented("\(Self.self).resetRecentlyUsed") }
	)
}

extension DependencyValues {
	public var recentlyUsedService: RecentlyUsedService {
		get { self[RecentlyUsedService.self] }
		set { self[RecentlyUsedService.self] = newValue }
	}
}
