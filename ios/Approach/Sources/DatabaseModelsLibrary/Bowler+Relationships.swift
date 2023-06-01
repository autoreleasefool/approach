import GRDB
import ModelsLibrary

extension Bowler.Database {
	public static let leagues = hasMany(League.Database.self)

	public static let series = hasMany(
		Series.Database.self,
		through: leagues,
		using: League.Database.series
	)

	public static let games = hasMany(
		Game.Database.self,
		through: series,
		using: Series.Database.games
	)

	public static let gear = hasMany(Gear.Database.self)
}
