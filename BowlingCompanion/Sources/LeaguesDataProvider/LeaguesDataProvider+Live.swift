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
				return persistenceService.fetchLeagues(.init(request))
			case .byRecentlyUsed:
				return .init { continuation in
					let task = Task {
						do {
							for try await (recentlyUsed, leagues) in combineLatest(
								recentlyUsedService.observeRecentlyUsed(.leagues),
								persistenceService.fetchLeagues(.init(request))
							) {
								continuation.yield(leagues.sortBy(ids: recentlyUsed))
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
