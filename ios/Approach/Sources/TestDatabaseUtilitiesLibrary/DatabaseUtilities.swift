import DatabaseLibrary
import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

public enum InitialValue<T> {
	case `default`
	case custom([T])
	case zero
}

public func initializeDatabase(
	withAvatars: InitialValue<Avatar.Database>? = nil,
	withLocations: InitialValue<Location.Database>? = nil,
	withAlleys: InitialValue<Alley.Database>? = nil,
	withLanes: InitialValue<Lane.Database>? = nil,
	withBowlers: InitialValue<Bowler.Database>? = nil,
	withGear: InitialValue<Gear.Database>? = nil,
	withLeagues: InitialValue<League.Database>? = nil,
	withSeries: InitialValue<Series.Database>? = nil,
	withGames: InitialValue<Game.Database>? = nil,
	withGameLanes: InitialValue<GameLane.Database>? = nil,
	withGameGear: InitialValue<GameGear.Database>? = nil,
	withFrames: InitialValue<Frame.Database>? = nil,
	withMatchPlays: InitialValue<MatchPlay.Database>? = nil,
	withStatisticsWidgets: InitialValue<StatisticsWidget.Database>? = nil,
	withBowlerPreferredGear: InitialValue<BowlerPreferredGear.Database>? = nil,
	to db: (any DatabaseWriter)? = nil
) throws -> any DatabaseWriter {
	let dbQueue: any DatabaseWriter
	if let db {
		dbQueue = db
	} else {
		dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		migrator.registerDBMigrations()
		try migrator.migrate(dbQueue)
	}

	let matchPlays = withMatchPlays
	let frames = withFrames
	let games = coallesce(withGames, ifHasOneOf: frames, matchPlays)
	let series = coallesce(withSeries, ifHasOneOf: games)
	let leagues = coallesce(withLeagues, ifHasOneOf: series)
	let gear = coallesce(withGear, ifHasOneOf: frames)
	let avatars = coallesce(withAvatars, ifHasOneOf: gear)
	let bowlers = coallesce(withBowlers, ifHasOneOf: leagues, gear, matchPlays)
	let lanes = withLanes
	let alleys = coallesce(withAlleys, ifHasOneOf: lanes, leagues)
	let locations = coallesce(withLocations, ifHasOneOf: alleys)
	let gameLanes = coallesce(withGameLanes, ifHasAllOf: games, lanes)
	let gameGear = coallesce(withGameGear, ifHasAllOf: games, gear)
	let bowlerPreferredGear = coallesce(withBowlerPreferredGear, ifHasAllOf: bowlers, gear)
	let statisticsWidgets = withStatisticsWidgets

	#if DEBUG
	try dbQueue.write {
		try insert(avatars: avatars, into: $0)
		try insert(locations: locations, into: $0)
		try insert(alleys: alleys, into: $0)
		try insert(lanes: lanes, into: $0)
		try insert(bowlers: bowlers, into: $0)
		try insert(gear: gear, into: $0)
		try insert(leagues: leagues, into: $0)
		try insert(series: series, into: $0)
		try insert(games: games, into: $0)
		try insert(gameLanes: gameLanes, into: $0)
		try insert(gameGear: gameGear, into: $0)
		try insert(frames: frames, into: $0)
		try insert(matchPlays: matchPlays, into: $0)
		try insert(statisticsWidgets: statisticsWidgets, into: $0)
		try insert(bowlerPreferredGear: bowlerPreferredGear, into: $0)
	}
	#endif

	return dbQueue
}

func coallesce<T>(_ value: InitialValue<T>?, ifHasAllOf: Any?...) -> InitialValue<T>? {
	if ifHasAllOf.compactMap({ $0 }).count < ifHasAllOf.count {
		return value
	} else {
		return value == nil ? .default : value
	}
}

func coallesce<T>(_ value: InitialValue<T>?, ifHasOneOf: Any?...) -> InitialValue<T>? {
	if ifHasOneOf.compactMap({ $0 }).isEmpty {
		return value
	} else {
		return value == nil ? .default : value
	}
}
