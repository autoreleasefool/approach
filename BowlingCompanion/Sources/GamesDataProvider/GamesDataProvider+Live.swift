import Dependencies
import GamesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension GamesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchGames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			return persistenceService.fetchGames(.init(series: request.series))
		}
	)
}
