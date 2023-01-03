import AsyncAlgorithms
import Dependencies
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension GearDataProvider: DependencyKey {
	public static let liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchGear: { request in
				let gear = try await persistenceService.fetchGear(request)

				switch request.ordering {
				case .byName:
					return gear
				case .byRecentlyUsed:
					let recentlyUsed = recentlyUsedService.getRecentlyUsed(.gear)
					return gear.sortBy(ids: recentlyUsed.map(\.id))
				}
			},
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
