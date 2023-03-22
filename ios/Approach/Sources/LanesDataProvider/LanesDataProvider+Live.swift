import Dependencies
import Foundation
import LanesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension LanesDataProvider: DependencyKey {
	public static var liveValue = Self(
		fetchLanes: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let lanes = try await persistenceService.fetchLanes(request)

			switch request.ordering {
			case .byLabel:
				return lanes
			}
		}
	)
}
