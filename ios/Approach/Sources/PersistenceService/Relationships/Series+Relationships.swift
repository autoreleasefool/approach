import GRDB
import SharedModelsLibrary

extension Series {
	static let bowler = hasOne(Bowler.self, through: league, using: League.bowler)
	static let league = belongsTo(League.self)
	static let seriesLanes = hasMany(SeriesLane.self)
	static let lanes = hasMany(Lane.self, through: seriesLanes, using: SeriesLane.lane)
	static let games = hasMany(Game.self)

	var bowler: QueryInterfaceRequest<Bowler> {
		request(for: Series.bowler)
	}

	var lanes: QueryInterfaceRequest<Lane> {
		request(for: Series.lanes)
	}

	var games: QueryInterfaceRequest<Game> {
		request(for: Series.games)
	}
}
