@preconcurrency import GRDB
import ModelsLibrary

extension Frame.Database {
	public static let game = belongsTo(Game.Database.self)
	public static let series = hasOne(Series.Database.self, through: game, using: Game.Database.series)

	public static let ball0ForeignKey = ForeignKey(["ball0"])
	public static let bowlingBall0 = belongsTo(Gear.Database.self, using: ball0ForeignKey)

	public static let ball1ForeignKey = ForeignKey(["ball1"])
	public static let bowlingBall1 = belongsTo(Gear.Database.self, using: ball1ForeignKey)

	public static let ball2ForeignKey = ForeignKey(["ball2"])
	public static let bowlingBall2 = belongsTo(Gear.Database.self, using: ball2ForeignKey)
}
