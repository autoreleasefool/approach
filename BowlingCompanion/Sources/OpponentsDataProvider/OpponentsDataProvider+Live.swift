import AsyncAlgorithms
import Dependencies
import OpponentsDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension OpponentsDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return .init(
			fetchOpponents: { request in
				@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
				@Dependency(\.persistenceService) var persistenceService: PersistenceService

				let opponents = try await persistenceService.fetchOpponents(request)

				switch request.ordering {
				case .byName:
					return opponents
				case .byRecentlyUsed:
					let recentlyUsed = recentlyUsedService.getRecentlyUsed(.opponents)
					return opponents.sortBy(ids: recentlyUsed.map(\.id))
				}
			},
			observeOpponents: { request in
				@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
				@Dependency(\.persistenceService) var persistenceService: PersistenceService

				switch request.ordering {
				case .byName:
					return persistenceService.observeOpponents(request)
				case .byRecentlyUsed:
					return .init { continuation in
						let task = Task {
							do {
								for try await (recentlyUsed, opponents) in combineLatest(
									recentlyUsedService.observeRecentlyUsed(.opponents),
									persistenceService.observeOpponents(request)
								) {
									continuation.yield(opponents.sortBy(ids: recentlyUsed.map(\.id)))
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
