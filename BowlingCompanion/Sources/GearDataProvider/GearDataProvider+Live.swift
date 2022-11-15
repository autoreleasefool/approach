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
				return persistenceService.fetchGear(.init(request))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						for try await (recentlyUsed, gear) in combineLatest(
							recentlyUsedService.observeRecentlyUsed(.gear),
							persistenceService.fetchGear(.init(request))
						) {
							continuation.yield(gear.sortBy(ids: recentlyUsed))
						}
					}

					continuation.onTermination = { _ in task.cancel() }
				}
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
