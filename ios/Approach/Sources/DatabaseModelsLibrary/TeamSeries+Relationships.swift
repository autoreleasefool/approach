import GRDB
import ModelsLibrary

extension TeamSeries.Database {
	public static let team = belongsTo(Team.Database.self)

	public static let teamSeries = hasMany(TeamSeriesSeries.Database.self)
		.order(\.position.asc)

	public static let series = hasMany(
		Series.Database.self,
		through: teamSeries,
		using: TeamSeriesSeries.Database.series
	)
}
