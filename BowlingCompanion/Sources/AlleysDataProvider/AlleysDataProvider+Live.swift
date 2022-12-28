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
			let alleys = try await persistenceService.fetchAlleys(request)

			switch request.ordering {
			case .byName:
				return alleys
			case .byRecentlyUsed:
				let recentlyUsed = recentlyUsedService.getRecentlyUsed(.alleys)
				return alleys.sortBy(ids: recentlyUsed)
			}
		},
		observeAlleys: { request in
			@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
			@Dependency(\.persistenceService) var persistenceService: PersistenceService

			switch request.ordering {
			case .byName:
				return persistenceService.observeAlleys(request)
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						do {
							for try await (recentlyUsed, alleys) in combineLatest(
								recentlyUsedService.observeRecentlyUsed(.alleys),
								persistenceService.observeAlleys(request)
							) {
								continuation.yield(alleys.sortBy(ids: recentlyUsed))
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
