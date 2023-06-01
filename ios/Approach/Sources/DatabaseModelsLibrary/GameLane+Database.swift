import GRDB
import ModelsLibrary

extension GameLane {
	public struct Database: Sendable, Codable, Equatable {
		public let gameId: Game.ID
		public let laneId: Lane.ID

		public init(gameId: Game.ID, laneId: Lane.ID) {
			self.gameId = gameId
			self.laneId = laneId
		}
	}
}

extension GameLane.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "gameLane"
}

extension GameLane.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let laneId = Column(CodingKeys.laneId)
	}
}
