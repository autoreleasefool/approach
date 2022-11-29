import AsyncAlgorithms
import Dependencies
import Foundation
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortingLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchLeagues: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byName:
				return try await persistenceService.fetchLeagues(.init(request))
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.leagues)
				let leagues = try await persistenceService.fetchLeagues(.init(request))
				return leagues.sortBy(ids: recentlyUsed)
			}
		}
	)
}

extension League.Query {
	init(_ request: League.FetchRequest) {
		let ordering: League.Query.Ordering
		switch request.ordering {
		case .byRecentlyUsed:
			ordering = .byName
		case .byName:
			ordering = .byName
		}

		self.init(bowler: request.bowler, ordering: ordering)
	}
}
