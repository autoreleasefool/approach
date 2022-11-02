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
					try alleysPersistenceService.create(alley, $0)
				}
			},
			update: { alley in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.alleysPersistenceService) var alleysPersistenceService: AlleysPersistenceService

				try await persistenceService.write {
					try alleysPersistenceService.update(alley, $0)
				}
			},
			delete: { alley in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.alleysPersistenceService) var alleysPersistenceService: AlleysPersistenceService

				try await persistenceService.write {
					try alleysPersistenceService.delete(alley, $0)
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
