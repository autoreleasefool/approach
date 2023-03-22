import AveragesDataProviderInterface
import Dependencies
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary

extension AveragesDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchAverages: persistenceService.fetchAverages,
			observeAverages: persistenceService.observeAverages
		)
	}()
}
