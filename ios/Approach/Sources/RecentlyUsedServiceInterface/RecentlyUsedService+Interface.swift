import Dependencies
import Foundation
import StatisticsLibrary

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

public struct RecentlyUsedTrackableFilterService: Sendable {
	public var didRecentlyUse: @Sendable (TrackableFilter) -> Void
	public var observeRecentlyUsed: @Sendable () -> AsyncStream<[TrackableFilter]>

	public init(
		didRecentlyUse: @escaping @Sendable (TrackableFilter) -> Void,
		observeRecentlyUsed: @escaping @Sendable () -> AsyncStream<[TrackableFilter]>
	) {
		self.didRecentlyUse = didRecentlyUse
		self.observeRecentlyUsed = observeRecentlyUsed
	}
}

extension RecentlyUsedService {
	public enum Resource: String, Sendable {
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
	public static var testValue: Self {
		Self(
			didRecentlyUseResource: { _, _ in unimplemented("\(Self.self).didRecentlyUseResource") },
			getRecentlyUsed: { _ in unimplemented("\(Self.self).getRecentlyUsed") },
			observeRecentlyUsed: { _ in unimplemented("\(Self.self).observeRecentlyUsed") },
			observeRecentlyUsedIds: { _ in unimplemented("\(Self.self).observeRecentlyUsedIds") },
			resetRecentlyUsed: { _ in unimplemented("\(Self.self).resetRecentlyUsed") }
		)
	}
}

extension RecentlyUsedTrackableFilterService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			didRecentlyUse: { _ in unimplemented("\(Self.self).didRecentlyUse") },
			observeRecentlyUsed: { unimplemented("\(Self.self).observeRecentlyUsed") }
		)
	}
}

extension DependencyValues {
	public var recentlyUsed: RecentlyUsedService {
		get { self[RecentlyUsedService.self] }
		set { self[RecentlyUsedService.self] = newValue }
	}

	public var recentlyUsedTrackableFilters: RecentlyUsedTrackableFilterService {
		get { self[RecentlyUsedTrackableFilterService.self] }
		set { self[RecentlyUsedTrackableFilterService.self] = newValue }
	}
}
