import Dependencies
import Foundation
import LanesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension LanesDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return Self(
			fetchLanes: { request in
				let lanes = try await persistenceService.fetchLanes(request)

				switch request.ordering {
				case .byLabel:
					return lanes
				}
			},
			observeLanes: persistenceService.observeLanes
		)
	}()
}
