import Dependencies
import GamesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension GamesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchGames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			return try await persistenceService.fetchGames(.init(request))
		}
	)
}

extension Game.Query {
	init(_ request: Game.FetchRequest) {
		self.init(series: request.series)
	}
}
