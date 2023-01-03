import GRDB
import SharedModelsLibrary
import SharedModelsFetchableLibrary

extension Average: FetchableRecord {
	public init(row: Row) throws {
		self.init(gamesPlayed: row[0], totalPinfall: row[1])
	}
}

extension Average.FetchRequest: Queryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Average] {
		// TODO: calculate averages for given entity IDs
		switch entityIds {
		case let .bowlers(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		case let .alleys(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		case let .gear(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		case let .lanes(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		case let .series(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		case let .leagues(ids): return ids.map { _ in .init(gamesPlayed: 0, totalPinfall: 0) }
		}
	}
}
