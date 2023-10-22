import Foundation
import GRDB
import ModelsLibrary

extension Frame {
	public struct Database: Sendable, Identifiable, Codable, Equatable {

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

extension Frame.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "frame"
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

extension DerivableRequest<Frame.Database> {
	public func orderByIndex() -> Self {
		order(Frame.Database.Columns.index)
	}

	public func filter(byGame: Game.ID) -> Self {
		filter(Frame.Database.Columns.gameId == byGame)
	}
}
