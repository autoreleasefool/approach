import Dependencies
import GamesDataProviderInterface
import PersistenceServiceInterface

extension GamesDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			observeGames: { request in
				switch request.ordering {
				case .byOrdinal:
					return persistenceService.observeGames(request)
				}
			}
		)
	}()
}
