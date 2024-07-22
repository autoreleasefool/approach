import Dependencies
import Foundation
import ModelsLibrary

extension Series {
	public enum Ordering: Hashable, CaseIterable {
		case newestFirst
		case oldestFirst
		case lowestToHighest
		case highestToLowest

		public static var `default`: Self = .newestFirst
	}
}

public struct SeriesRepository: Sendable {
	public var list: @Sendable (League.ID, Series.Ordering) -> AsyncThrowingStream<[Series.List], Error>
	public var summaries: @Sendable (League.ID) -> AsyncThrowingStream<[Series.Summary], Error>
	public var unusedPreBowls: @Sendable (League.ID) -> AsyncThrowingStream<[Series.Summary], Error>
	public var gameHost: @Sendable (Series.ID) async throws -> Series.GameHost
	public var eventSeries: @Sendable (League.ID) async throws -> Series.GameHost
	public var shareable: @Sendable (Series.ID) async throws -> Series.Shareable
	public var archived: @Sendable () -> AsyncThrowingStream<[Series.Archived], Error>
	public var edit: @Sendable (Series.ID) async throws -> Series.Edit
	public var usePreBowl: @Sendable (Series.ID, Date) async throws -> Void
	public var create: @Sendable (Series.Create) async throws -> Void
	public var update: @Sendable (Series.Edit) async throws -> Void
	public var addGamesToSeries: @Sendable (Series.ID, Int) async throws -> Void
	public var archive: @Sendable (Series.ID) async throws -> Void
	public var unarchive: @Sendable (Series.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (League.ID, Series.Ordering) -> AsyncThrowingStream<[Series.List], Error>,
		summaries: @escaping @Sendable (League.ID) -> AsyncThrowingStream<[Series.Summary], Error>,
		unusedPreBowls: @escaping @Sendable (League.ID) -> AsyncThrowingStream<[Series.Summary], Error>,
		gameHost: @escaping @Sendable (Series.ID) async throws -> Series.GameHost,
		eventSeries: @escaping @Sendable (League.ID) async throws -> Series.GameHost,
		shareable: @escaping @Sendable (Series.ID) async throws -> Series.Shareable,
		archived: @escaping @Sendable () -> AsyncThrowingStream<[Series.Archived], Error>,
		edit: @escaping @Sendable (Series.ID) async throws -> Series.Edit,
		usePreBowl: @escaping @Sendable (Series.ID, Date) async throws -> Void,
		create: @escaping @Sendable (Series.Create) async throws -> Void,
		update: @escaping @Sendable (Series.Edit) async throws -> Void,
		addGamesToSeries: @escaping @Sendable (Series.ID, Int) async throws -> Void,
		archive: @escaping @Sendable (Series.ID) async throws -> Void,
		unarchive: @escaping @Sendable (Series.ID) async throws -> Void
	) {
		self.list = list
		self.summaries = summaries
		self.unusedPreBowls = unusedPreBowls
		self.gameHost = gameHost
		self.eventSeries = eventSeries
		self.shareable = shareable
		self.archived = archived
		self.edit = edit
		self.usePreBowl = usePreBowl
		self.create = create
		self.update = update
		self.addGamesToSeries = addGamesToSeries
		self.archive = archive
		self.unarchive = unarchive
	}

	public func list(bowledIn: League.ID, orderedBy: Series.Ordering) -> AsyncThrowingStream<[Series.List], Error> {
		self.list(bowledIn, orderedBy)
	}

	public func summaries(bowledIn: League.ID) -> AsyncThrowingStream<[Series.Summary], Error> {
		self.summaries(bowledIn)
	}

	public func unusedPreBowls(bowledIn: League.ID) -> AsyncThrowingStream<[Series.Summary], Error> {
		self.unusedPreBowls(bowledIn)
	}
}

extension SeriesRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			list: { _, _ in unimplemented("\(Self.self).list") },
			summaries: { _ in unimplemented("\(Self.self).summaries") },
			unusedPreBowls: { _ in unimplemented("\(Self.self).unusedPreBowls") },
			gameHost: { _ in unimplemented("\(Self.self).gameHost") },
			eventSeries: { _ in unimplemented("\(Self.self).eventSeries") },
			shareable: { _ in unimplemented("\(Self.self).shareable") },
			archived: { unimplemented("\(Self.self).archived") },
			edit: { _ in unimplemented("\(Self.self).edit") },
			usePreBowl: { _, _ in unimplemented("\(Self.self).usePreBowl") },
			create: { _ in unimplemented("\(Self.self).create") },
			update: { _ in unimplemented("\(Self.self).update") },
			addGamesToSeries: { _, _ in unimplemented("\(Self.self).addGames") },
			archive: { _ in unimplemented("\(Self.self).archive") },
			unarchive: { _ in unimplemented("\(Self.self).unarchive") }
		)
	}
}
