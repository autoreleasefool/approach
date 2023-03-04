import Dependencies
import ExtensionsLibrary
import GRDB
import SharedModelsLibrary

extension Series: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw ValidationError.usingPlaceholderId }
	}

	public func aroundInsert(_ db: Database, insert: () throws -> InsertionSuccess) throws {
		@Dependency(\.uuid) var uuid: UUIDGenerator
		@Dependency(\.date) var date: DateGenerator

		_ = try insert()

		for ordinal in (1...numberOfGames) {
			let game = Game(
				series: id,
				id: uuid(),
				ordinal: ordinal,
				locked: .unlocked,
				manualScore: nil,
				excludeFromStatistics: .init(from: excludeFromStatistics)
			)
			try game.insert(db)
		}
	}
}
