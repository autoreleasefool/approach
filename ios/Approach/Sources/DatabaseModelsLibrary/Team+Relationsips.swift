import GRDB
import ModelsLibrary

extension Team.Database {
	public static let teamBowlers = hasMany(TeamBowler.Database.self)
		.order(\.position.asc)

	public static let members = hasMany(
		Bowler.Database.self,
		through: teamBowlers,
		using: TeamBowler.Database.bowler
	)

	public static let teamSeries = hasMany(TeamSeries.Database.self)

	public static let series = hasMany(
		Series.Database.self,
		through: teamSeries,
		using: TeamSeries.Database.series
	)
}
