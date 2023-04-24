import GRDB
import SharedModelsLibrary

extension Game: FetchableRecord, PersistableRecord {
	public func aroundInsert(_ db: Database, insert: () throws -> InsertionSuccess) throws {
		_ = try insert()

		for ordinal in (1...Game.NUMBER_OF_FRAMES) {
			let frame = Frame(
				game: id,
				ordinal: ordinal,
				rolls: []
			)

			try frame.insert(db)
		}
	}
}
