import Dependencies
import ExtensionsLibrary
import GRDB
import SharedModelsLibrary

extension League: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw ValidationError.usingPlaceholderId }
	}

	public func aroundInsert(_ db: Database, insert: () throws -> InsertionSuccess) throws {
		_ = try insert()

		if recurrence == .oneTimeEvent, let numberOfGames {
			@Dependency(\.uuid) var uuid: UUIDGenerator
			@Dependency(\.date) var date: DateGenerator

			let series = Series(
				league: id,
				id: uuid(),
				date: date(),
				numberOfGames: numberOfGames,
				preBowl: .regularPlay,
				excludeFromStatistics: .init(from: excludeFromStatistics),
				alley: alley,
				lane: nil
			)
			try series.insert(db)
		}
	}
}
