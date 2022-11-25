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
				return persistenceService.fetchBowlers(.init(request))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						do {
							for try await (recentlyUsed, bowlers) in combineLatest(
								recentlyUsedService.observeRecentlyUsed(.bowlers),
								persistenceService.fetchBowlers(.init(request))
							) {
								continuation.yield(bowlers.sortBy(ids: recentlyUsed))
							}
						} catch {
							continuation.finish(throwing: error)
						}
					}

					continuation.onTermination = { _ in task.cancel() }
				}
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
