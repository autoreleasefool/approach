import BowlersDataProviderInterface
import Dependencies
import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			create: { bowler in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.bowlersPersistenceService) var bowlersPersistenceService: BowlersPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try bowlersPersistenceService.create(bowler, db)
					}
				}
			},
			update: { bowler in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.bowlersPersistenceService) var bowlersPersistenceService: BowlersPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try bowlersPersistenceService.update(bowler, db)
					}
				}
			},
			delete: { bowler in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.bowlersPersistenceService) var bowlersPersistenceService: BowlersPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try bowlersPersistenceService.delete(bowler, db)
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

							for try await bowlers in observation.values(in: db) {
								continuation.yield(bowlers)
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
