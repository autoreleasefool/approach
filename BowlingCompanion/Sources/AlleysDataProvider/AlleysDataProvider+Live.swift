import AlleysDataProviderInterface
import Dependencies
import Foundation
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortingLibrary

extension AlleysDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchAlleys: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let alleys = try await persistenceService.fetchAlleys(request)

			switch request.ordering {
			case .byName:
				return alleys
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.alleys)
				return alleys.sortBy(ids: recentlyUsed)
			}
		}
	)
}
