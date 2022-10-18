import Dependencies
import ExtensionsLibrary
import GamesDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension GamesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService

		return Self(
			create: { series, game in },
			delete: { game in },
			fetchAll: { series in
				.init { continuation in
					continuation.finish()
				}
			}
		)
	}()
}
