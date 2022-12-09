import Dependencies
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension GearDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchGear: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let gear = try await persistenceService.fetchGear(request)

			switch request.ordering {
			case .byName:
				return gear
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.gear)
				return gear.sortBy(ids: recentlyUsed)
			}
		}
	)
}
