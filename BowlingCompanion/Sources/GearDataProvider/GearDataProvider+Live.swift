import AsyncAlgorithms
import Dependencies
import Foundation
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SortingLibrary

extension GearDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchGear: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byName:
				return try await persistenceService.fetchGear(.init(request))
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.gear)
				let gear = try await persistenceService.fetchGear(.init(request))
				return gear.sortBy(ids: recentlyUsed)
			}
		}
	)
}

extension Gear.Query {
	init(_ request: Gear.FetchRequest) {
		let ordering: Gear.Query.Ordering
		switch request.ordering {
		case .byName:
			ordering = .byName
		case .byRecentlyUsed:
			ordering = .byName
		}

		self.init(bowler: request.bowler, kind: request.kind, ordering: ordering)
	}
}
