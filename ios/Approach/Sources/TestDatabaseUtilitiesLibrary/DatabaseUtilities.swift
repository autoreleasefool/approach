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
	withLocations: InitialValue<Location.Database>? = nil,
	withAlleys: InitialValue<Alley.Database>? = nil,
	withLanes: InitialValue<Lane.Database>? = nil,
	withBowlers: InitialValue<Bowler.Database>? = nil,
	withGear: InitialValue<Gear.Database>? = nil,
	withLeagues: InitialValue<League.Database>? = nil,
	withSeries: InitialValue<Series.Database>? = nil,
	withGames: InitialValue<Game.Database>? = nil,
	withFrames: InitialValue<Frame.Database>? = nil
) throws -> any DatabaseWriter {
	let dbQueue = try DatabaseQueue()
	var migrator = DatabaseMigrator()
	migrator.registerDBMigrations()
	try migrator.migrate(dbQueue)

	let frames = withFrames
	let games = coallesce(withGames, ifHas: frames)
	let series = coallesce(withSeries, ifHas: games)
	let leagues = coallesce(withLeagues, ifHas: series)
	let gear = coallesce(withGear, ifHas: frames)
	let bowlers = coallesce(withBowlers, ifHas: leagues, gear)
	let lanes = coallesce(withLanes, ifHas: series)
	let alleys = coallesce(withAlleys, ifHas: lanes, leagues)
	let locations = coallesce(withLocations, ifHas: alleys)

	try dbQueue.write {
		try insert(locations: locations, into: $0)
		try insert(alleys: alleys, into: $0)
		try insert(lanes: lanes, into: $0)
		try insert(bowlers: bowlers, into: $0)
		try insert(gear: gear, into: $0)
		try insert(leagues: leagues, into: $0)
		try insert(series: series, into: $0)
		try insert(games: games, into: $0)
		try insert(frames: frames, into: $0)
	}

	return dbQueue
}

func coallesce<T>(_ value: InitialValue<T>?, ifHas: Optional<Any>...) -> InitialValue<T>? {
	if ifHas.compactMap({ $0 }).isEmpty {
		return value
	} else {
		return value == nil ? .default : value
	}
}
