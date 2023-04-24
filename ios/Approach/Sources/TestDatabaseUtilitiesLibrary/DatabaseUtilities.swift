import DatabaseLibrary
import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

public enum InitialValue<T> {
	case `default`
	case custom([T])
}

public func initializeDatabase(
	withAlleys: InitialValue<Alley.Database>? = nil,
	withLanes: InitialValue<Lane.Database>? = nil,
	withBowlers: InitialValue<Bowler.Database>? = nil,
	withGear: InitialValue<Gear.Database>? = nil,
	withLeagues: InitialValue<League.Database>? = nil,
	withSeries: InitialValue<Series.Database>? = nil,
	withGames: InitialValue<Game.Database>? = nil,
	withFrames: InitialValue<Frame.Database>? = nil
) async throws -> any DatabaseWriter {
	let dbQueue = try DatabaseQueue()
	var migrator = DatabaseMigrator()
	migrator.registerDBMigrations()
	try migrator.migrate(dbQueue)

	try await dbQueue.write {
		try insert(alleys: withAlleys, into: $0)
		try insert(lanes: withLanes, into: $0)
		try insert(bowlers: withBowlers, into: $0)
		try insert(gear: withGear, into: $0)
		try insert(leagues: withLeagues, into: $0)
		try insert(series: withSeries, into: $0)
		try insert(games: withGames, into: $0)
		try insert(frames: withFrames, into: $0)
	}

	return dbQueue
}
