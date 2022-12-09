import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct GamesDataProvider: Sendable {
	public var fetchGames: @Sendable (Game.FetchRequest) async throws -> [Game]

	public init(
		fetchGames: @escaping @Sendable (Game.FetchRequest) async throws -> [Game]
	) {
		self.fetchGames = fetchGames
	}
}

extension GamesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchGames: { _ in fatalError("\(Self.self).fetchGames") }
	)
}

extension DependencyValues {
	public var gamesDataProvider: GamesDataProvider {
		get { self[GamesDataProvider.self] }
		set { self[GamesDataProvider.self] = newValue }
	}
}
