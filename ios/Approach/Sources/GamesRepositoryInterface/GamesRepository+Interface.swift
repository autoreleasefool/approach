import Dependencies
import Foundation
import ModelsLibrary

extension Game {
	public enum Ordering: Hashable, CaseIterable {
		case byIndex
	}
}

public enum GamesRepositoryError: Error, LocalizedError {
	case tooManyGamesToReorder(series: Series.ID, gamesNotInSeries: Set<Game.ID>)
	case missingGamesToReorder(series: Series.ID, gamesInSeriesMissing: Set<Game.ID>)
	case reorderingDuplicateGames(series: Series.ID, duplicateGames: Set<Game.ID>)

	public var errorDescription: String? {
		switch self {
		case let .tooManyGamesToReorder(series, gamesNotInSeries):
			return "Could not reorder games, games do not belong to series \(series): \(gamesNotInSeries)"
		case let .missingGamesToReorder(series, gamesInSeriesMissing):
			return "Could not reorder games, not all games in series provided \(series): \(gamesInSeriesMissing)"
		case let .reorderingDuplicateGames(series, duplicateGames):
			return "Could not reorder duplicate games in serites \(series): \(duplicateGames)"
		}
	}
}

public struct GamesRepository: Sendable {
	public var list: @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.List], Error>
	public var archived: @Sendable () -> AsyncThrowingStream<[Game.Archived], Error>
	public var summariesList: @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.Summary], Error>
	public var matchesAgainstOpponent: @Sendable (Bowler.ID) -> AsyncThrowingStream<[Game.ListMatch], Error>
	public var shareGames: @Sendable ([Game.ID]) async throws -> [Game.Shareable]
	public var shareSeries: @Sendable (Series.ID) async throws -> [Game.Shareable]
	public var observe: @Sendable (Game.ID) -> AsyncThrowingStream<Game.Edit?, Error>
	public var findIndex: @Sendable (Game.ID) async throws -> Game.Indexed?
	public var update: @Sendable (Game.Edit) async throws -> Void
	public var archive: @Sendable (Game.ID) async throws -> Void
	public var unarchive: @Sendable (Game.ID) async throws -> Void
	public var duplicateLanes: @Sendable (Game.ID, [Game.ID]) async throws -> Void
	public var reorderGames: @Sendable (Series.ID, [Game.ID]) async throws -> Void

	public init(
		list: @escaping @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.List], Error>,
		archived: @escaping @Sendable () -> AsyncThrowingStream<[Game.Archived], Error>,
		summariesList: @escaping @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.Summary], Error>,
		matchesAgainstOpponent: @escaping @Sendable (Bowler.ID) -> AsyncThrowingStream<[Game.ListMatch], Error>,
		shareGames: @escaping @Sendable ([Game.ID]) async throws -> [Game.Shareable],
		shareSeries: @escaping @Sendable (Series.ID) async throws -> [Game.Shareable],
		observe: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<Game.Edit?, Error>,
		findIndex: @escaping @Sendable (Game.ID) async throws -> Game.Indexed?,
		update: @escaping @Sendable (Game.Edit) async throws -> Void,
		archive: @escaping @Sendable (Game.ID) async throws -> Void,
		unarchive: @escaping @Sendable (Game.ID) async throws -> Void,
		duplicateLanes: @escaping @Sendable (Game.ID, [Game.ID]) async throws -> Void,
		reorderGames: @escaping @Sendable (Series.ID, [Game.ID]) async throws -> Void
	) {
		self.list = list
		self.archived = archived
		self.summariesList = summariesList
		self.matchesAgainstOpponent = matchesAgainstOpponent
		self.shareGames = shareGames
		self.shareSeries = shareSeries
		self.observe = observe
		self.findIndex = findIndex
		self.update = update
		self.archive = archive
		self.unarchive = unarchive
		self.duplicateLanes = duplicateLanes
		self.reorderGames = reorderGames
	}

	public func seriesGames(forId: Series.ID, ordering: Game.Ordering) -> AsyncThrowingStream<[Game.List], Error> {
		self.list(forId, ordering)
	}

	public func seriesGamesSummaries(
		forId: Series.ID,
		ordering: Game.Ordering
	) -> AsyncThrowingStream<[Game.Summary], Error> {
		self.summariesList(forId, ordering)
	}

	public func matches(against opponent: Bowler.ID) -> AsyncThrowingStream<[Game.ListMatch], Error> {
		self.matchesAgainstOpponent(opponent)
	}

	public func duplicateLanes(from source: Game.ID, toAllGames allGames: [Game.ID]) async throws {
		try await self.duplicateLanes(source, allGames)
	}

	public func reorderGames(_ games: [Game.ID], inSeries series: Series.ID) async throws {
		try await self.reorderGames(series, games)
	}
}

extension GamesRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _ in unimplemented("\(Self.self).list") },
		archived: { unimplemented("\(Self.self).archived") },
		summariesList: { _, _ in unimplemented("\(Self.self).summariesList") },
		matchesAgainstOpponent: { _ in unimplemented("\(Self.self).matchesAgainstOpponent") },
		shareGames: { _ in unimplemented("\(Self.self).shareGames") },
		shareSeries: { _ in unimplemented("\(Self.self).shareSeries") },
		observe: { _ in unimplemented("\(Self.self).observeChanges") },
		findIndex: { _ in unimplemented("\(Self.self).findIndex") },
		update: { _ in unimplemented("\(Self.self).update") },
		archive: { _ in unimplemented("\(Self.self).archive") },
		unarchive: { _ in unimplemented("\(Self.self).unarchive") },
		duplicateLanes: { _, _ in unimplemented("\(Self.self).duplicateLanes") },
		reorderGames: { _, _ in unimplemented("\(Self.self).reorderGames") }
	)
}
