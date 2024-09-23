@preconcurrency import GRDB
import ModelsLibrary

extension MatchPlay.Database {
	public static let opponentKey = ForeignKey(["opponentId"])
	public static let opponent = belongsTo(Bowler.Database.self, using: opponentKey)

	public static let game = belongsTo(Game.Database.self)
	public static let series = hasOne(Series.Database.self, through: game, using: Game.Database.series)
	public static let league = hasOne(League.Database.self, through: series, using: Series.Database.league)
	public static let bowler = hasOne(Bowler.Database.self, through: league, using: League.Database.bowler)
}
