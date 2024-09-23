@preconcurrency import GRDB
import ModelsLibrary

extension Series.Database {
	public static let bowler = hasOne(Bowler.Database.self, through: league, using: League.Database.bowler)
	public static let games = hasMany(Game.Database.self)
	public static let league = belongsTo(League.Database.self)

	public static let alley = belongsTo(Alley.Database.self)
	public static let lanes = hasMany(Lane.Database.self, through: games, using: Game.Database.lanes)
}
