import GRDB
import ModelsLibrary

extension Bowler.Database {
	public static let trackableLeagues = hasMany(League.Database.self)
		.filter(League.Database.Columns.excludeFromStatistics == League.ExcludeFromStatistics.include)

	public static let trackableSeries = hasMany(
		Series.Database.self,
		through: trackableLeagues,
		using: League.Database.trackableSeries
	)

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
