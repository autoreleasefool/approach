import Dependencies
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { bowler, db in
			try bowler.insert(db)
		},
		update: { bowler, db in
			try bowler.update(db)
		},
		delete: { bowler, db in
			try bowler.delete(db)
		}
	)
}
