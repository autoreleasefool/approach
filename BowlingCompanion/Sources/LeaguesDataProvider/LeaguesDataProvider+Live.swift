import Dependencies
import LeaguesDataProviderInterface
import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary

extension LeaguesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			create: { league in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.leaguesPersistenceService) var leaguesPersistenceService: LeaguesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try leaguesPersistenceService.create(league, db)
					}
				}
			},
			update: { league in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.leaguesPersistenceService) var leaguesPersistenceService: LeaguesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try leaguesPersistenceService.update(league, db)
					}
				}
			},
			delete: { league in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.leaguesPersistenceService) var leaguesPersistenceService: LeaguesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try leaguesPersistenceService.delete(league, db)
					}
				}
			},
			fetchAll: { request in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				return .init { continuation in
					Task {
						do {
							let db = persistenceService.reader()
							let observation = ValueObservation.tracking(request.fetchValue(_:))

							for try await leagues in observation.values(in: db) {
								continuation.yield(leagues)
							}

							continuation.finish()
						} catch {
							continuation.finish(throwing: error)
						}
					}
				}
			}
		)
	}()
}
