import AsyncAlgorithms
import Dependencies
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension LeaguesDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
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
