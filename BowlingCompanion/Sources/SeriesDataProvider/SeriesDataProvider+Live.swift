import Dependencies
import SeriesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.uuid) var uuid
		@Dependency(\.persistenceService) var persistenceService

		return Self(
			create: { league, series in },
			delete: { series in },
			fetchAll: { league in
				.init { continuation in
					continuation.finish()
				}
			}
		)
	}()
}
