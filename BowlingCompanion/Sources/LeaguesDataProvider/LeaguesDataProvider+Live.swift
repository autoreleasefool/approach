import Dependencies
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchLeagues: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let leagues = try await persistenceService.fetchLeagues(request)

			switch request.ordering {
			case .byName:
				return leagues
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.leagues)
				return leagues.sortBy(ids: recentlyUsed)
			}
		}
	)
}
