import FramesDataProviderInterface
import Dependencies
import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary

extension FramesDataProvider: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			create: { frame in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.framesPersistenceService) var framesPersistenceService: FramesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try framesPersistenceService.create(frame, db)
					}
				}
			},
			delete: { frame in
				@Dependency(\.persistenceService) var persistenceService: PersistenceService
				@Dependency(\.framesPersistenceService) var framesPersistenceService: FramesPersistenceService

				try await persistenceService.write {
					try await $0.write { db in
						try framesPersistenceService.delete(frame, db)
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

							for try await frames in observation.values(in: db) {
								continuation.yield(frames)
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
