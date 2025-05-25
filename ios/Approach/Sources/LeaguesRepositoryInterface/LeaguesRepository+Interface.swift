import Dependencies
import ModelsLibrary

extension League {
	public enum Ordering: String, Hashable, CaseIterable, Sendable {
		case byName
		case byRecentlyUsed
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

	public func list(filter: ListFilter) -> AsyncThrowingStream<[League.List], Error> {
		self.list(bowledBy: filter.bowler, withRecurrence: filter.recurrence, ordering: filter.ordering)
	}
}

extension LeaguesRepository {
	public struct ListFilter: Sendable {
		public let bowler: Bowler.ID
		public let recurrence: League.Recurrence?
		public let ordering: League.Ordering

		public init(bowler: Bowler.ID, recurrence: League.Recurrence?, ordering: League.Ordering) {
			self.bowler = bowler
			self.recurrence = recurrence
			self.ordering = ordering
		}
	}
}

extension LeaguesRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			list: { _, _, _ in unimplemented("\(Self.self).list", placeholder: .never) },
			pickable: { _, _, _ in unimplemented("\(Self.self).pickable", placeholder: .never) },
			archived: { unimplemented("\(Self.self).archived", placeholder: .never) },
			seriesHost: { _ in unimplemented("\(Self.self).seriesHost", placeholder: .placeholder) },
			edit: { _ in unimplemented("\(Self.self).edit", placeholder: .placeholder) },
			create: { _ in unimplemented("\(Self.self).create") },
			update: { _ in unimplemented("\(Self.self).update") },
			archive: { _ in unimplemented("\(Self.self).archive") },
			unarchive: { _ in unimplemented("\(Self.self).unarchive") }
		)
	}
}
