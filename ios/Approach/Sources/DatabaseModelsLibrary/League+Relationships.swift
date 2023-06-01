import GRDB
import ModelsLibrary

extension League.Database {
	public static let bowler = belongsTo(Bowler.Database.self)
	public static let series = hasMany(Series.Database.self)
	public static let alleys = hasMany(
		Alley.Database.self,
		through: series,
		using: Series.Database.alley
	)

	public static let trackableSeries = hasMany(Series.Database.self)
		.filter(Series.Database.Columns.excludeFromStatistics == Series.ExcludeFromStatistics.include)

	public static let trackableGames = hasMany(
		Game.Database.self,
		through: trackableSeries,
		using: Series.Database.trackableGames
	)

	public static let trackableFrames = hasMany(
		Frame.Database.self,
		through: trackableGames,
		using: Game.Database.frames
	)
}
