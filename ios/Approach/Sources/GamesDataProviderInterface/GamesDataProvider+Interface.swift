import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct GamesDataProvider: Sendable {
	public var fetchGames: @Sendable (Game.FetchRequest) async throws -> [Game]
	public var observeGames: @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>

	public init(
		fetchGames: @escaping @Sendable (Game.FetchRequest) async throws -> [Game],
		observeGames: @escaping @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>
	) {
		self.fetchGames = fetchGames
		self.observeGames = observeGames
	}
}

extension GamesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchGames: { _ in unimplemented("\(Self.self).fetchGames") },
		observeGames: { _ in unimplemented("\(Self.self).observeGames") }
	)
}

extension DependencyValues {
	public var gamesDataProvider: GamesDataProvider {
		get { self[GamesDataProvider.self] }
		set { self[GamesDataProvider.self] = newValue }
	}
}
