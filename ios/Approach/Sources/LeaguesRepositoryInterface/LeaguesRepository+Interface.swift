import Dependencies
import ModelsLibrary

extension League {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed

		public static var `default`: Self { .byRecentlyUsed }
	}
}

public struct LeaguesRepository: Sendable {
	public var list: @Sendable (
		Bowler.ID,
		League.Recurrence?,
		League.Ordering
	) -> AsyncThrowingStream<[League.List], Error>
	public var pickable: @Sendable (
		Bowler.ID,
		League.Recurrence?,
		League.Ordering
	) -> AsyncThrowingStream<[League.Summary], Error>
	public var archived: @Sendable () -> AsyncThrowingStream<[League.Archived], Error>
	public var seriesHost: @Sendable (League.ID) async throws -> League.SeriesHost
	public var edit: @Sendable (League.ID) async throws -> League.Edit
	public var create: @Sendable (League.Create) async throws -> Void
	public var update: @Sendable(League.Edit) async throws -> Void
	public var archive: @Sendable (League.ID) async throws -> Void
	public var unarchive: @Sendable (League.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (
			Bowler.ID,
			League.Recurrence?,
			League.Ordering
		) -> AsyncThrowingStream<[League.List], Error>,
		pickable: @escaping @Sendable (
			Bowler.ID,
			League.Recurrence?,
			League.Ordering
		) -> AsyncThrowingStream<[League.Summary], Error>,
		archived: @escaping @Sendable () -> AsyncThrowingStream<[League.Archived], Error>,
		seriesHost: @escaping @Sendable(League.ID) async throws -> League.SeriesHost,
		edit: @escaping @Sendable (League.ID) async throws -> League.Edit,
		create: @escaping @Sendable (League.Create) async throws -> Void,
		update: @escaping @Sendable (League.Edit) async throws -> Void,
		archive: @escaping @Sendable (League.ID) async throws -> Void,
		unarchive: @escaping @Sendable (League.ID) async throws -> Void
	) {
		self.list = list
		self.pickable = pickable
		self.archived = archived
		self.seriesHost = seriesHost
		self.edit = edit
		self.create = create
		self.update = update
		self.archive = archive
		self.unarchive = unarchive
	}

	public func list(
		bowledBy: Bowler.ID,
		withRecurrence: League.Recurrence? = nil,
		ordering: League.Ordering
	) -> AsyncThrowingStream<[League.List], Error> {
		self.list(bowledBy, withRecurrence, ordering)
	}

	public func pickable(
		bowledBy: Bowler.ID,
		withRecurrence: League.Recurrence? = nil,
		ordering: League.Ordering
	) -> AsyncThrowingStream<[League.Summary], Error> {
		self.pickable(bowledBy, withRecurrence, ordering)
	}
}

extension LeaguesRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _, _ in unimplemented("\(Self.self).list") },
		pickable: { _, _, _ in unimplemented("\(Self.self).pickable") },
		archived: { unimplemented("\(Self.self).archived") },
		seriesHost: { _ in unimplemented("\(Self.self).seriesHost") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		archive: { _ in unimplemented("\(Self.self).archive") },
		unarchive: { _ in unimplemented("\(Self.self).unarchive") }
	)
}
