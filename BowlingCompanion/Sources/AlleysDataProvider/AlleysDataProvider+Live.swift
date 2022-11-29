import AlleysDataProviderInterface
import AsyncAlgorithms
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

			switch request.ordering {
			case .byName:
				return try await persistenceService.fetchAlleys(.init(request))
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.alleys)
				let alleys = try await persistenceService.fetchAlleys(.init(request))
				return alleys.sortBy(ids: recentlyUsed)
			}
		}
	)
}

extension Alley.Query {
	init(_ request: Alley.FetchRequest) {
		let ordering: Alley.Query.Ordering
		switch request.ordering {
		case .byRecentlyUsed:
			ordering = .byName
		case .byName:
			ordering = .byName
		}

		self.init(ordering: ordering)
	}
}
