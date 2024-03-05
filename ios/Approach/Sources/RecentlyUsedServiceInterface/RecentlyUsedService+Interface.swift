import Dependencies
import Foundation

public struct RecentlyUsedService: Sendable {
	public var didRecentlyUseResource: @Sendable (Resource, UUID) -> Void
	public var getRecentlyUsed: @Sendable (Resource) -> [Entry]
	public var observeRecentlyUsed: @Sendable (Resource) -> AsyncStream<[Entry]>
	public var observeRecentlyUsedIds: @Sendable (Resource) -> AsyncStream<[UUID]>
	public var resetRecentlyUsed: @Sendable (Resource) -> Void

	public init(
		didRecentlyUseResource: @escaping @Sendable (Resource, UUID) -> Void,
		getRecentlyUsed: @escaping @Sendable (Resource) -> [Entry],
		observeRecentlyUsed: @escaping @Sendable (Resource) -> AsyncStream<[Entry]>,
		observeRecentlyUsedIds: @escaping @Sendable (Resource) -> AsyncStream<[UUID]>,
		resetRecentlyUsed: @escaping @Sendable (Resource) -> Void
	) {
		self.didRecentlyUseResource = didRecentlyUseResource
		self.getRecentlyUsed = getRecentlyUsed
		self.observeRecentlyUsed = observeRecentlyUsed
		self.observeRecentlyUsedIds = observeRecentlyUsedIds
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
		observeRecentlyUsedIds: { _ in unimplemented("\(Self.self).observeRecentlyUsedIds") },
		resetRecentlyUsed: { _ in unimplemented("\(Self.self).resetRecentlyUsed") }
	)
}
