import GRDB
import SharedModelsLibrary

extension Game {
	static let bowler = hasOne(Bowler.self, through: league, using: League.bowler)
	static let league = hasOne(League.self, through: series, using: Series.league)
	static let series = belongsTo(Series.self)
	static let frames = hasMany(Frame.self)

	var bowler: QueryInterfaceRequest<Bowler> {
		request(for: Game.bowler)
	}

	var frames: QueryInterfaceRequest<Frame> {
		request(for: Game.frames)
	}
}
