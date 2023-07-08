import Dependencies
import ModelsLibrary

extension Game {
	public enum Ordering: Hashable, CaseIterable {
		case byIndex
	}
}

public struct GamesRepository: Sendable {
	public var list: @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.List], Error>
	public var summariesList: @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.Summary], Error>
	public var matchesAgainstOpponent: @Sendable (Bowler.ID) -> AsyncThrowingStream<[Game.ListMatch], Error>
	public var edit: @Sendable (Game.ID) async throws -> Game.Edit?
	public var update: @Sendable (Game.Edit) async throws -> Void
	public var delete: @Sendable (Game.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.List], Error>,
		summariesList: @escaping @Sendable (Series.ID, Game.Ordering) -> AsyncThrowingStream<[Game.Summary], Error>,
		matchesAgainstOpponent: @escaping @Sendable (Bowler.ID) -> AsyncThrowingStream<[Game.ListMatch], Error>,
		edit: @escaping @Sendable (Game.ID) async throws -> Game.Edit?,
		update: @escaping @Sendable (Game.Edit) async throws -> Void,
		delete: @escaping @Sendable (Game.ID) async throws -> Void
	) {
		self.list = list
		self.summariesList = summariesList
		self.matchesAgainstOpponent = matchesAgainstOpponent
		self.edit = edit
		self.update = update
		self.delete = delete
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
}

extension GamesRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _ in unimplemented("\(Self.self).list") },
		summariesList: { _, _ in unimplemented("\(Self.self).summariesList") },
		matchesAgainstOpponent: { _ in unimplemented("\(Self.self).matchesAgainstOpponent") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var games: GamesRepository {
		get { self[GamesRepository.self] }
		set { self[GamesRepository.self] = newValue }
	}
}
