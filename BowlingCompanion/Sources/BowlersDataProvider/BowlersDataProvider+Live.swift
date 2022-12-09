import BowlersDataProviderInterface
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchBowlers: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let bowlers = try await persistenceService.fetchBowlers(request)

			switch request.ordering {
			case .byName:
				return bowlers
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.bowlers)
				return bowlers.sortBy(ids: recentlyUsed)
			}
		}
	)
}
