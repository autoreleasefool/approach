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
				return persistenceService.fetchAlleys(.init(ordering: .byName))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						for try await (recentlyUsed, alleys) in combineLatest(
							recentlyUsedService.observeRecentlyUsed(.alleys),
							persistenceService.fetchAlleys(.init(ordering: .byName))
						) {
							continuation.yield(alleys.sortBy(ids: recentlyUsed))
						}
					}

					continuation.onTermination = { _ in task.cancel() }
				}
			}
		}
	)
}
