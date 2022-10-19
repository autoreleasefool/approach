import Dependencies
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.uuid) var uuid
		@Dependency(\.date) var date
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			create: { bowler, league in },
			delete: { league in },
			fetchAll: { bowler in
				.init { continuation in
					continuation.finish()
				}
			}
		)
	}()
}
