import Dependencies
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension AlleysPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { alley, db in
			try alley.insert(db)
		},
		update: { alley, db in
			try alley.update(db)
		},
		delete: { alley, db in
			try alley.delete(db)
		}
	)
}
