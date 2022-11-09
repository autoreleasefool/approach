import Dependencies
import GRDB
import SharedModelsLibrary

extension League: FetchableRecord, PersistableRecord {
	public func willInsert(_ db: Database) throws {
		if recurrence == .oneTimeEvent, let numberOfGames {
			@Dependency(\.uuid) var uuid: UUIDGenerator
			@Dependency(\.date) var date: DateGenerator

			let series = Series(leagueId: id, id: uuid(), date: date(), numberOfGames: numberOfGames)
			try series.insert(db)
		}
	}
}
