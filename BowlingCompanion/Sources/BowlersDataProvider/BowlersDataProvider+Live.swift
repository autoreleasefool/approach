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
					try bowlersPersistenceService.create(bowler, $0)
				}
			},
			update: { bowler in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.bowlersPersistenceService) var bowlersPersistenceService: BowlersPersistenceService

				try await persistenceService.write {
					try bowlersPersistenceService.update(bowler, $0)
				}
			},
			delete: { bowler in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.bowlersPersistenceService) var bowlersPersistenceService: BowlersPersistenceService

				try await persistenceService.write {
					try bowlersPersistenceService.delete(bowler, $0)
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
