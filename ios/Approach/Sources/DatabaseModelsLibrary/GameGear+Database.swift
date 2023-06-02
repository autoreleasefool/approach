import GRDB
import ModelsLibrary

extension GameGear {
	public struct Database: Sendable, Codable, Equatable {
		public let gameId: Game.ID
		public let gearId: Gear.ID

		public init(gameId: Game.ID, gearId: Gear.ID) {
			self.gameId = gameId
			self.gearId = gearId
		}
	}
}

extension GameGear.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "gameGear"
}

extension GameGear.Database {
	public enum Columns {
		public static let gameId = Column(CodingKeys.gameId)
		public static let gearId = Column(CodingKeys.gearId)
	}
}
