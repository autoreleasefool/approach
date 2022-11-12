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
				return persistenceService.fetchAlleys(.init(request))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						for try await (recentlyUsed, alleys) in combineLatest(
							recentlyUsedService.observeRecentlyUsed(.alleys),
							persistenceService.fetchAlleys(.init(request))
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
