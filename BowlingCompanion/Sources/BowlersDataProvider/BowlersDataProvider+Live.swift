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
				return persistenceService.fetchBowlers(.init(ordering: .byName))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						for try await (recentlyUsed, bowlers) in combineLatest(
							recentlyUsedService.observeRecentlyUsed(.bowlers),
							persistenceService.fetchBowlers(.init(ordering: .byName))
						) {
							continuation.yield(bowlers.sortBy(ids: recentlyUsed))
						}
					}

					continuation.onTermination = { _ in task.cancel() }
				}
			}
		}
	)
}
