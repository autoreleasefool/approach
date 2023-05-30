import Foundation
import GRDB
import ModelsLibrary

extension Frame {
	public struct Database: Sendable, Identifiable, Codable, Equatable, TableRecord {
		public static let databaseTableName = "frame"

		public let gameId: Game.ID
		public let index: Int
		public var roll0: String?
		public var roll1: String?
		public var roll2: String?
		public var ball0: Gear.ID?
		public var ball1: Gear.ID?
		public var ball2: Gear.ID?

		public var id: String { Frame.buildId(game: gameId, index: index) }

		public init(
			gameId: Game.ID,
			index: Int,
			roll0: String?,
			roll1: String?,
			roll2: String?,
			ball0: Gear.ID?,
			ball1: Gear.ID?,
			ball2: Gear.ID?
		) {
			self.gameId = gameId
			self.index = index
			self.roll0 = roll0
			self.roll1 = roll1
			self.roll2 = roll2
			self.ball0 = ball0
			self.ball1 = ball1
			self.ball2 = ball2
		}
	}
}

extension Frame.Database: FetchableRecord, PersistableRecord {
	public static let game = belongsTo(Game.Database.self)
	public static let series = hasOne(Series.Database.self, through: game, using: Game.Database.series)

	public static let ball0ForeignKey = ForeignKey(["ball0"])
	public static let bowlingBall0 = belongsTo(Gear.Database.self, using: ball0ForeignKey)

	public static let ball1ForeignKey = ForeignKey(["ball1"])
	public static let bowlingBall1 = belongsTo(Gear.Database.self, using: ball1ForeignKey)

	public static let ball2ForeignKey = ForeignKey(["ball2"])
	public static let bowlingBall2 = belongsTo(Gear.Database.self, using: ball2ForeignKey)
}

extension Frame.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let index = Column(CodingKeys.index)
		public static let roll0 = Column(CodingKeys.roll0)
		public static let roll1 = Column(CodingKeys.roll1)
		public static let roll2 = Column(CodingKeys.roll2)
		public static let ball0 = Column(CodingKeys.ball0)
		public static let ball1 = Column(CodingKeys.ball1)
		public static let ball2 = Column(CodingKeys.ball2)
	}
}
