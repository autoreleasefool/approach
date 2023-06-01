import GRDB
import ModelsLibrary

extension Series.Database {
	public static let league = belongsTo(League.Database.self)
	public static let games = hasMany(Game.Database.self)

	public static let alley = belongsTo(Alley.Database.self)
	public static let lanes = hasMany(Lane.Database.self, through: games, using: Game.Database.lanes)

	public static let trackableGames = hasMany(Game.Database.self)
		.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)
		.order(Game.Database.Columns.index.asc)

	public static let trackableFrames = hasMany(Frame.Database.self, through: trackableGames, using: Game.Database.frames)
}
