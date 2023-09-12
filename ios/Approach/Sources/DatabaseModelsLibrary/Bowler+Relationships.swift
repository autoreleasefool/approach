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
	public static let bowlerPreferredGear = hasMany(BowlerPreferredGear.Database.self)
	public static let preferredGear = hasMany(
		Gear.Database.self,
		through: bowlerPreferredGear,
		using: BowlerPreferredGear.Database.gear
	)

	public static let matchOpponentKey = ForeignKey(["opponentId"])
	public static let matchesAsOpponent = hasMany(
		MatchPlay.Database.self,
		using: matchOpponentKey
	)

	public static let gamesAsOpponent = hasMany(
		Game.Database.self,
		through: matchesAsOpponent,
		using: MatchPlay.Database.game
	)
}
