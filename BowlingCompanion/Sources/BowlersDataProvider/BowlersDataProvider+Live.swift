import AsyncAlgorithms
import BowlersDataProviderInterface
import Dependencies
import Foundation
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortingLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchBowlers: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byName:
				return try await persistenceService.fetchBowlers(.init(request))
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.bowlers)
				let bowlers = try await persistenceService.fetchBowlers(.init(request))
				return bowlers.sortBy(ids: recentlyUsed)
			}
		}
	)
}

extension Bowler.Query {
	init(_ request: Bowler.FetchRequest) {
		let ordering: Bowler.Query.Ordering
		switch request.ordering {
		case .byRecentlyUsed:
			ordering = .byName
		case .byName:
			ordering = .byName
		}

		self.init(ordering: ordering)
	}
}
