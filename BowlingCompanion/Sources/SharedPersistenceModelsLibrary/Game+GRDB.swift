import GRDB
import SharedModelsLibrary

extension Game: FetchableRecord, PersistableRecord {
	public func willInsert(_ db: Database) throws {
		for ordinal in (1...Game.NUMBER_OF_FRAMES) {
			let frame = Frame(
				gameId: id,
				ordinal: ordinal,
				firstBall: nil,
				secondBall: nil,
				thirdBall: nil
			)
			try frame.insert(db)
		}
	}
}
