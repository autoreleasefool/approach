@preconcurrency import GRDB
import ModelsLibrary

extension League.Database {
	public static let bowler = belongsTo(Bowler.Database.self)
	public static let series = hasMany(Series.Database.self)
	public static let games = hasMany(Game.Database.self, through: series, using: Series.Database.games)

	public static let alleys = hasMany(
		Alley.Database.self,
		through: series,
		using: Series.Database.alley
	)
}
