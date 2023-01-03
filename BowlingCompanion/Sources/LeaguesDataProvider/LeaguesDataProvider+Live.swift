import AsyncAlgorithms
import Dependencies
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchLeagues: { request in
				let leagues = try await persistenceService.fetchLeagues(request)

				switch request.ordering {
				case .byName:
					return leagues
				case .byRecentlyUsed:
					let recentlyUsed = recentlyUsedService.getRecentlyUsed(.leagues)
					return leagues.sortBy(ids: recentlyUsed.map(\.id))
				}
			},
			observeLeagues: { request in
				switch request.ordering {
				case .byName:
					return persistenceService.observeLeagues(request)
				case .byRecentlyUsed:
					return .init { continuation in
						let task = Task {
							do {
								for try await (recentlyUsed, leagues) in combineLatest(
									recentlyUsedService.observeRecentlyUsed(.leagues),
									persistenceService.observeLeagues(request)
								) {
									continuation.yield(leagues.sortBy(ids: recentlyUsed.map(\.id)))
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
	}()
}
