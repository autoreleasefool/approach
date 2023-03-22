import AsyncAlgorithms
import BowlersDataProviderInterface
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension BowlersDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchBowlers: { request in
				let bowlers = try await persistenceService.fetchBowlers(request)

				switch request.ordering {
				case .byName:
					return bowlers
				case .byRecentlyUsed:
					let recentlyUsed = recentlyUsedService.getRecentlyUsed(.bowlers)
					return bowlers.sortBy(ids: recentlyUsed.map(\.id))
				}
			},
			observeBowlers: { request in
				switch request.ordering {
				case .byName:
					return persistenceService.observeBowlers(request)
				case .byRecentlyUsed:
					return .init { continuation in
						let task = Task {
							do {
								for try await (recentlyUsed, bowlers) in combineLatest(
									recentlyUsedService.observeRecentlyUsed(.bowlers),
									persistenceService.observeBowlers(request)
								) {
									continuation.yield(bowlers.sortBy(ids: recentlyUsed.map(\.id)))
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
