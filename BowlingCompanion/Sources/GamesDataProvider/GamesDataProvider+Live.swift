import Dependencies
import GamesDataProviderInterface
import PersistenceServiceInterface

extension GamesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchGames: { request in
				let games = try await persistenceService.fetchGames(request)

				switch request.ordering {
				case .byOrdinal:
					return games
				}
			},
			observeGames: { request in
				switch request.ordering {
				case .byOrdinal:
					return persistenceService.observeGames(request)
				}
			}
		)
	}()
}
