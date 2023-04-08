import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct GamesDataProvider: Sendable {
	public var observeGames: @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>

	public init(
		observeGames: @escaping @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>
	) {
		self.observeGames = observeGames
	}
}

extension GamesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeGames: { _ in unimplemented("\(Self.self).observeGames") }
	)
}

extension DependencyValues {
	public var gamesDataProvider: GamesDataProvider {
		get { self[GamesDataProvider.self] }
		set { self[GamesDataProvider.self] = newValue }
	}
}
