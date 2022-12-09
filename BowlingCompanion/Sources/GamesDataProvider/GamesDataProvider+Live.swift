import Dependencies
import GamesDataProviderInterface
import PersistenceServiceInterface

extension GamesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchGames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let games = try await persistenceService.fetchGames(request)

			switch request.ordering {
			case .byOrdinal:
				return games
			}
		}
	)
}
