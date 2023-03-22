import AsyncAlgorithms
import TeamsDataProviderInterface
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SortingLibrary

extension TeamsDataProvider: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.recentlyUsedService) var recentlyUsedService: RecentlyUsedService
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		return .init(
			fetchTeams: { request in
				let teams = try await persistenceService.fetchTeams(request)

				switch request.ordering {
				case .byName:
					return teams
				case .byRecentlyUsed:
					let recentlyUsed = recentlyUsedService.getRecentlyUsed(.teams)
					return teams.sortBy(ids: recentlyUsed.map(\.id))
				}
			},
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
