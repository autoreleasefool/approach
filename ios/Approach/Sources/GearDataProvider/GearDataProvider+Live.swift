import AsyncAlgorithms
import Dependencies
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension GearDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			observeGear: { request in
				switch request.ordering {
				case .byName:
					return persistenceService.observeGear(request)
				case .byRecentlyUsed:
					return .init { continuation in
						let task = Task {
							do {
								for try await (recentlyUsed, gear) in combineLatest(
									recentlyUsedService.observeRecentlyUsed(.gear),
									persistenceService.observeGear(request)
								) {
									continuation.yield(gear.sortBy(ids: recentlyUsed.map(\.id)))
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
