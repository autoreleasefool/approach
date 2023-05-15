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
	withSeriesLanes: InitialValue<SeriesLane.Database>? = nil,
	withGames: InitialValue<Game.Database>? = nil,
	withFrames: InitialValue<Frame.Database>? = nil,
	withMatchPlays: InitialValue<MatchPlay.Database>? = nil
) throws -> any DatabaseWriter {
	let dbQueue = try DatabaseQueue()
	var migrator = DatabaseMigrator()
	migrator.registerDBMigrations()
	try migrator.migrate(dbQueue)

	let matchPlays = withMatchPlays
	let frames = withFrames
	let games = coallesce(withGames, ifHasOneOf: frames, matchPlays)
	let series = coallesce(withSeries, ifHasOneOf: games)
	let leagues = coallesce(withLeagues, ifHasOneOf: series)
	let gear = coallesce(withGear, ifHasOneOf: frames)
	let bowlers = coallesce(withBowlers, ifHasOneOf: leagues, gear, matchPlays)
	let lanes = coallesce(withLanes, ifHasOneOf: series)
	let alleys = coallesce(withAlleys, ifHasOneOf: lanes, leagues)
	let locations = coallesce(withLocations, ifHasOneOf: alleys)
	let seriesLanes = coallesce(withSeriesLanes, ifHasAllOf: series, lanes)

	try dbQueue.write {
		try insert(locations: locations, into: $0)
		try insert(alleys: alleys, into: $0)
		try insert(lanes: lanes, into: $0)
		try insert(bowlers: bowlers, into: $0)
		try insert(gear: gear, into: $0)
		try insert(leagues: leagues, into: $0)
		try insert(series: series, into: $0)
		try insert(seriesLanes: seriesLanes, into: $0)
		try insert(games: games, into: $0)
		try insert(frames: frames, into: $0)
		try insert(matchPlays: matchPlays, into: $0)
	}

	return dbQueue
}

func coallesce<T>(_ value: InitialValue<T>?, ifHasAllOf: Optional<Any>...) -> InitialValue<T>? {
	if ifHasAllOf.compactMap({ $0 }).count < ifHasAllOf.count {
		return value
	} else {
		return value == nil ? .default : value
	}
}

func coallesce<T>(_ value: InitialValue<T>?, ifHasOneOf: Optional<Any>...) -> InitialValue<T>? {
	if ifHasOneOf.compactMap({ $0 }).isEmpty {
		return value
	} else {
		return value == nil ? .default : value
	}
}
