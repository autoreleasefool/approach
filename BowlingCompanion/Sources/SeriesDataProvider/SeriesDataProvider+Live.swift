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
					try seriesPersistenceService.create(series, $0)
				}
			},
			update: { series in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

				try await persistenceService.write {
					try seriesPersistenceService.update(series, $0)
				}
			},
			delete: { series in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

				try await persistenceService.write {
					try seriesPersistenceService.delete(series, $0)
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
