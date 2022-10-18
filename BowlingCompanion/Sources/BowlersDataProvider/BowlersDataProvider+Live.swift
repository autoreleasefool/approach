import BowlersDataProviderInterface
import Dependencies
import ExtensionsLibrary
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { bowler in },
			update: { bowler in },
			delete: { bowler in },
			fetchAll: {
				.init { continuation in
					continuation.finish()
				}
			}
		)
	}()
}
