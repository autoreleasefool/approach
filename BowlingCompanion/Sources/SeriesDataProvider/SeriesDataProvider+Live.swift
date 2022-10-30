import Dependencies
import GRDB
import PersistenceServiceInterface
import SeriesDataProviderInterface
import SharedModelsLibrary

extension SeriesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			create: { series in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try seriesPersistenceService.create(series, db)
					}
				}
			},
			update: { series in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try seriesPersistenceService.update(series, db)
					}
				}
			},
			delete: { series in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try seriesPersistenceService.delete(series, db)
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

							for try await series in observation.values(in: db) {
								continuation.yield(series)
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
