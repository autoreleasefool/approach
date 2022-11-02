import AlleysDataProviderInterface
import Dependencies
import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary

extension AlleysDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			create: { alley in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.alleysPersistenceService) var alleysPersistenceService: AlleysPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try alleysPersistenceService.create(alley, db)
					}
				}
			},
			update: { alley in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.alleysPersistenceService) var alleysPersistenceService: AlleysPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try alleysPersistenceService.update(alley, db)
					}
				}
			},
			delete: { alley in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.alleysPersistenceService) var alleysPersistenceService: AlleysPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try alleysPersistenceService.delete(alley, db)
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

							for try await alleys in observation.values(in: db) {
								continuation.yield(alleys)
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
