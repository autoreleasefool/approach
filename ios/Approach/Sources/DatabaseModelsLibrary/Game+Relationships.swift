import GRDB
import ModelsLibrary

extension Game.Database {
	public static let series = belongsTo(Series.Database.self)

	public static let league = hasOne(League.Database.self, through: series, using: Series.Database.league)

	public static let bowler = hasOne(Bowler.Database.self, through: league, using: League.Database.bowler)

	public static let frames = hasMany(Frame.Database.self).order(Frame.Database.Columns.index.asc)

	public static let matchPlay = hasOne(MatchPlay.Database.self)

	public static let alley = hasOne(Alley.Database.self, through: series, using: Series.Database.alley)

	public static let lanes = hasMany(Lane.Database.self, through: series, using: Series.Database.lanes)
}
