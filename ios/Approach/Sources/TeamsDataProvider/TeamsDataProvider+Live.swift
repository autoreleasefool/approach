import AsyncAlgorithms
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary
import TeamsDataProviderInterface

extension TeamsDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			observeTeams: { request in
				switch request.ordering {
				case .byName:
					return persistenceService.observeTeams(request)
				case .byRecentlyUsed:
					return .init { continuation in
						let task = Task {
							do {
								for try await (recentlyUsed, teams) in combineLatest(
									recentlyUsedService.observeRecentlyUsed(.teams),
									persistenceService.observeTeams(request)
								) {
									continuation.yield(teams.sortBy(ids: recentlyUsed.map(\.id)))
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
