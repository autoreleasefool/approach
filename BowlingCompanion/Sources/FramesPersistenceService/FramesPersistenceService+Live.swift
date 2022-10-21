import Dependencies
import FramesPersistenceServiceInterface
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension FramesPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { frame, db in
			try frame.insert(db)
		},
		update: { frame, db in
			try frame.update(db)
		},
		delete: { frame, db in
			try frame.delete(db)
		}
	)
}
